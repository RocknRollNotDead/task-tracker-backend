package ru.codeportfolio.tasktracker.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.model.CustomUserDetails;
import ru.codeportfolio.tasktracker.model.User;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    public SecurityConfig(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/auth/**",
                                        "/v3/api-docs/**", "/swagger-ui/**", "/v3/api-docs.yaml",
                                        "/v3/api-docs/**", "/swagger-ui.html"
                                ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user").authenticated()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    objectMapper.writeValueAsString(
                                            buildResponse(
                                                    "User not authorized!"
                                            )));
                        })
                )
                .logout(logout -> logout
                                .logoutUrl("/auth/sign-out")
                                .logoutSuccessHandler((
                                        request, response, authentication) -> {
                                    if (authentication == null) {
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    } else {
                                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                                    }

                                })
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                User user = userRepository
                        .findUsersByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("email not exist " + email));
                return new CustomUserDetails(user);
            }
        };

    }

    private Map<String, String> buildResponse(String message) {
        return Map.of(
                "message", message);
    }
}
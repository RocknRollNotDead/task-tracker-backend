package ru.codeportfolio.tasktracker.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.codeportfolio.tasktracker.dto.http.request.RequestAuthDto;
import ru.codeportfolio.tasktracker.dto.jwt.JwtAuthenticationDto;
import ru.codeportfolio.tasktracker.security.CustomUserDetails;
import ru.codeportfolio.tasktracker.security.JwtService;

@Slf4j
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    public JwtAuthenticationDto auth(RequestAuthDto req) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(),
                        req.password()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return jwtService.generateJwtToken(
                userDetails.getEmail(),
                authentication.getAuthorities(),
                userDetails.getId()
        );
    }

}

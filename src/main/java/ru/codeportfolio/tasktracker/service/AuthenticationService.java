package ru.codeportfolio.tasktracker.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.codeportfolio.tasktracker.dto.RequestAuthDto;
import ru.codeportfolio.tasktracker.model.CustomUserDetails;

@Slf4j
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;


    public AuthenticationService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    public CustomUserDetails auth(RequestAuthDto req) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(),
                        req.password()
                )
        );
        return (CustomUserDetails) authentication.getPrincipal();
    }

}

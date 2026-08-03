package ru.codeportfolio.tasktracker.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.codeportfolio.tasktracker.dto.RequestAuthDto;
import ru.codeportfolio.tasktracker.dto.jwt.JwtAuthenticationDto;
import ru.codeportfolio.tasktracker.model.CustomUserDetails;
import ru.codeportfolio.tasktracker.service.AuthenticationService;
import ru.codeportfolio.tasktracker.service.JwtService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }


    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationDto> logIn(@Valid @RequestBody RequestAuthDto req) {

        CustomUserDetails principal = authenticationService.auth(req);

        JwtAuthenticationDto jwtDto = jwtService.generateAuthToken(
                principal.getEmail(),
                principal.getAuthorities(),
                principal.getId()
        );

        return ResponseEntity.ok(jwtDto);


    }


}
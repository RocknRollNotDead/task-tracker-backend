package ru.codeportfolio.tasktracker.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.codeportfolio.tasktracker.dto.http.request.RequestAuthDto;
import ru.codeportfolio.tasktracker.dto.jwt.JwtAuthenticationDto;
import ru.codeportfolio.tasktracker.security.CustomUserDetails;
import ru.codeportfolio.tasktracker.security.JwtService;
import ru.codeportfolio.tasktracker.service.AuthenticationService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationDto> logIn(@Valid @RequestBody RequestAuthDto req) {

        JwtAuthenticationDto jwtDto = authenticationService.auth(req);

        return ResponseEntity.ok(jwtDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(
            ) {
            return ResponseEntity.noContent().build();
    }


}
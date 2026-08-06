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
import ru.codeportfolio.tasktracker.service.AuthenticationService;
import ru.codeportfolio.tasktracker.security.JwtService;


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

        JwtAuthenticationDto jwtDto = jwtService.generateJwtToken(
                principal.getEmail(),
                principal.getAuthorities(),
                principal.getId()
        );

        return ResponseEntity.ok(jwtDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null){
            throw new BadCredentialsException("Impossible logout because you not authorized.");
        } else {
            return ResponseEntity.noContent().build();
        }
    }


}
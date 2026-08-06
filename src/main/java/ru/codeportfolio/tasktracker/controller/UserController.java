package ru.codeportfolio.tasktracker.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.codeportfolio.tasktracker.dto.http.request.RequestAuthDto;
import ru.codeportfolio.tasktracker.dto.http.request.RequestRegistrationDto;
import ru.codeportfolio.tasktracker.dto.http.response.UserDto;
import ru.codeportfolio.tasktracker.dto.jwt.JwtAuthenticationDto;
import ru.codeportfolio.tasktracker.security.CustomUserDetails;
import ru.codeportfolio.tasktracker.service.AuthenticationService;
import ru.codeportfolio.tasktracker.security.JwtService;
import ru.codeportfolio.tasktracker.service.UserService;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public UserController(UserService service, AuthenticationService authenticationService, JwtService jwtService) {
        this.service = service;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping()
    public ResponseEntity<JwtAuthenticationDto> createUser(
                                              @Valid @RequestBody RequestRegistrationDto req) {

        UserDto userDto = service.createUser(req);

        CustomUserDetails principal = authenticationService.auth(
                new RequestAuthDto(req.email(), req.password()));

        JwtAuthenticationDto jwtDto = jwtService.generateJwtToken(
                principal.getEmail(),
                principal.getAuthorities(),
                principal.getId()
        );

        return ResponseEntity.ok(jwtDto);
    }


    @GetMapping()
    public ResponseEntity<UserDto> getInfo(

            @NotNull @AuthenticationPrincipal CustomUserDetails principal) {

        UserDto userDto = service.getInfo(principal.getId());

        return ResponseEntity.ok(userDto);
    }

}

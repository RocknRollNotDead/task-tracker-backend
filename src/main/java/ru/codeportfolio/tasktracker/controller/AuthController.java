package ru.codeportfolio.tasktracker.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.codeportfolio.tasktracker.dto.RequestAuthDto;
import ru.codeportfolio.tasktracker.dto.UserDto;
import ru.codeportfolio.tasktracker.model.CustomUserDetails;
import ru.codeportfolio.tasktracker.service.AuthenticationService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @PostMapping("/login")
    public ResponseEntity<UserDto> logIn(HttpServletRequest httpRequest,
                                         HttpServletResponse response,
                                         @Valid @RequestBody RequestAuthDto req) {


        CustomUserDetails principal = authenticationService.auth(httpRequest, response, req);

        UserDto userDto = new UserDto(
                principal.getId(),
                principal.getEmail()
        );

        return ResponseEntity.ok(userDto);


    }


}
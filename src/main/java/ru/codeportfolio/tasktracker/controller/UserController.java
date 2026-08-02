package ru.codeportfolio.tasktracker.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.codeportfolio.tasktracker.dto.RequestAuthDto;
import ru.codeportfolio.tasktracker.dto.RequestRegistrationDto;
import ru.codeportfolio.tasktracker.dto.UserDto;
import ru.codeportfolio.tasktracker.model.CustomUserDetails;
import ru.codeportfolio.tasktracker.service.AuthenticationService;
import ru.codeportfolio.tasktracker.service.UserService;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;
    private final AuthenticationService authenticationService;

    public UserController(UserService service, AuthenticationService authenticationService) {
        this.service = service;
        this.authenticationService = authenticationService;
    }

    @PostMapping()
    public ResponseEntity<UserDto> createUser(HttpServletRequest httpRequest,
                                              HttpServletResponse response,
                                              @Valid @RequestBody RequestRegistrationDto req) {

        UserDto userDto = service.createUser(req);
        authenticationService.auth(httpRequest, response, new RequestAuthDto(req.email(), req.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }


    @GetMapping()
    public ResponseEntity<UserDto> getInfo(

            @AuthenticationPrincipal CustomUserDetails principal) throws AuthenticationException {

        UserDto userDto = service.getInfo(principal.getId());

        return ResponseEntity.ok(userDto);
    }

}

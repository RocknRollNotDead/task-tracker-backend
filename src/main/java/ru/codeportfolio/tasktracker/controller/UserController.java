package ru.codeportfolio.tasktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.codeportfolio.tasktracker.dto.RequestAuthDto;
import ru.codeportfolio.tasktracker.dto.UserDto;
import ru.codeportfolio.tasktracker.exception.ValidationException;
import ru.codeportfolio.tasktracker.service.UserService;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Зарегистрироваться")
    @ApiResponse(responseCode = "201", description = "Успех")
    @ApiResponse(responseCode = "400", description = "Невалидный аргумент (пароль или username)")
    @ApiResponse(responseCode = "409", description = "username занят другим пользователем")
    @PostMapping("/sign-up")
    public ResponseEntity<UserDto> createUser(
            @RequestBody(required = false) RequestAuthDto req) {

        if (req == null) {
            throw new ValidationException("Bad request - no username and password");
        }

        UserDto userDto = service.createUser(req.username(), req.password(), req.email());// email +
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }


    @Operation(summary = "Получить информация о юзере")
    @ApiResponse(responseCode = "200", description = "Успешно получена информация")
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getInfo(@AuthenticationPrincipal UserDetails principal) { // тот обьект который в SecurityConfig

        UserDto userDto = service.getInfo(principal.getUsername());
        return ResponseEntity.ok(userDto);
    }

}

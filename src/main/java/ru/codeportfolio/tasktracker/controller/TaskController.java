package ru.codeportfolio.tasktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.codeportfolio.tasktracker.dto.RequestAuthDto;
import ru.codeportfolio.tasktracker.dto.TaskDto;
import ru.codeportfolio.tasktracker.dto.UserDto;
import ru.codeportfolio.tasktracker.exception.ValidationException;
import ru.codeportfolio.tasktracker.service.TaskService;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "Создать заметку")
    @ApiResponse(responseCode = "201", description = "Успех")
    @ApiResponse(responseCode = "400", description = "Невалидный аргумент (пароль или username)")
    @ApiResponse(responseCode = "409", description = "username занят другим пользователем")
    @PostMapping()
    public ResponseEntity<UserDto> createTask(
            @RequestBody(required = false) RequestAuthDto req) {

        if (req == null) {
            throw new ValidationException("Bad request - no username and password");
        }

        UserDto userDto = service.createUser(req.username(), req.password(), req.email()); // email +
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }


    @Operation(summary = "Получить заметки")
    @ApiResponse(responseCode = "200", description = "Успешно получена информация")
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    @GetMapping()
    public ResponseEntity<UserDto> getTasks(@AuthenticationPrincipal UserDetails principal) { // тот обьект который в SecurityConfig

        UserDto userDto = service.getTasks(principal.getUsername());
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping()
    public ResponseEntity<TaskDto> patchTask(@AuthenticationPrincipal UserDetails principal){
        TaskDto dto = service.patchTask();
        return ResponseEntity.ok(dto);
    }

}

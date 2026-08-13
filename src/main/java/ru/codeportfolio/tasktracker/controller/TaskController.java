package ru.codeportfolio.tasktracker.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.codeportfolio.tasktracker.dto.http.request.RequestTaskDto;
import ru.codeportfolio.tasktracker.dto.http.response.TaskDto;
import ru.codeportfolio.tasktracker.exception.entity.ValidationException;
import ru.codeportfolio.tasktracker.security.CustomUserDetails;
import ru.codeportfolio.tasktracker.service.TaskService;

import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<TaskDto> createTask(
            @AuthenticationPrincipal CustomUserDetails principal,
            @NotNull(message = "Bad request - no body")
            @Valid @RequestBody(required = false) RequestTaskDto request) {

        TaskDto taskDto = service.create(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDto);
    }

    @GetMapping()
    public ResponseEntity<List<TaskDto>> getTasks(@AuthenticationPrincipal CustomUserDetails principal) {

        List<TaskDto> result = service.get(principal.getId());
        return ResponseEntity.ok(result);
    }

    @PatchMapping()
    public ResponseEntity<TaskDto> executeTask(@AuthenticationPrincipal CustomUserDetails principal,
                                               @NotNull @RequestParam Long taskId) {
        TaskDto dto = service.patch(principal.getId(), taskId);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/edit")
    public ResponseEntity<TaskDto> editTask(@AuthenticationPrincipal CustomUserDetails principal,
                                            @NotNull @RequestParam Long taskId,
                                            @NotNull @Valid @RequestBody RequestTaskDto dto) {
        TaskDto taskDto = service.edit(principal.getId(), taskId, dto);
        return ResponseEntity.ok(taskDto);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal CustomUserDetails principal,
                                           @NotNull @RequestParam Long taskId) {
        service.delete(principal.getId(), taskId);
        return ResponseEntity.noContent().build();
    }


}

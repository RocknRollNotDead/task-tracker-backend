package ru.codeportfolio.tasktracker.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.codeportfolio.tasktracker.dto.RequestTaskDto;
import ru.codeportfolio.tasktracker.dto.TaskDto;
import ru.codeportfolio.tasktracker.exception.entity.ValidationException;
import ru.codeportfolio.tasktracker.model.CustomUserDetails;
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
            @Valid @RequestBody(required = false) RequestTaskDto request) {

        if (request == null) {
            throw new ValidationException("Bad request - no body");
        }

        TaskDto taskDto = service.create(principal.getId(), request); // email +
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDto);
    }

    @GetMapping()
    public ResponseEntity<List<TaskDto>> getTasks(@AuthenticationPrincipal CustomUserDetails principal) { // тот обьект который в SecurityConfig

        List<TaskDto> result = service.get(principal.getId());
        return ResponseEntity.ok(result);
    }

    @PatchMapping()
    public ResponseEntity<TaskDto> executeTask(@AuthenticationPrincipal CustomUserDetails principal, @RequestParam Long taskId){
        TaskDto dto = service.patch(principal.getId(), taskId);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/edit")
    public ResponseEntity<TaskDto> editTask(@AuthenticationPrincipal CustomUserDetails principal,
                                            @RequestParam Long taskId,
                                            @RequestParam RequestTaskDto dto){
        TaskDto taskDto = service.edit(principal.getId(), taskId, dto);
        return ResponseEntity.ok(taskDto);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal CustomUserDetails principal, @RequestParam Long taskId){
        service.delete(principal.getId(), taskId);
        return ResponseEntity.noContent().build();
    }



}

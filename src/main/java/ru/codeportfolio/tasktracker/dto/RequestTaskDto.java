package ru.codeportfolio.tasktracker.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestTaskDto(

        @NotBlank(message = "Task name can't be empty!")
        String name,

        String text
) {
}

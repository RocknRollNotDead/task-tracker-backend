package ru.codeportfolio.tasktracker.dto.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestTaskDto(

        @NotBlank(message = "Task name can't be empty!")
        String name,

        @NotNull
        String text
) {
}

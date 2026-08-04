package ru.codeportfolio.tasktracker.dto;

public record EmailDto(
        String email,
        String username,
        String text
) {
}

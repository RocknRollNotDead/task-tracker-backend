package ru.codeportfolio.tasktracker.dto;

public record EmailDto(
        String email,
        String header,
        String text
) {
}

package ru.codeportfolio.tasktracker.dto;

public record RequestAuthDto(
        String username,
        String password,
        String email
) {
}

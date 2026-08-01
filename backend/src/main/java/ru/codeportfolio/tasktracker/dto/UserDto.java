package ru.codeportfolio.tasktracker.dto;

public record UserDto(
        Long id,
        String username,
        String email
) {
}

package ru.codeportfolio.tasktracker.dto.response;

public record UserDto(
        Long id,
        String username,
        String email
) {
}

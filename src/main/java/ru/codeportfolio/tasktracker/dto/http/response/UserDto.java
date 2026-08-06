package ru.codeportfolio.tasktracker.dto.http.response;

public record UserDto(
        Long id,
        String username,
        String email
) {
}

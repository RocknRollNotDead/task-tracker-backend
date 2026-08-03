package ru.codeportfolio.tasktracker.dto.jwt;

public record UserCredentialsDto(

        String email,
        String password
) {
}

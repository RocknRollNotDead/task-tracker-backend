package ru.codeportfolio.tasktracker.dto.http.response;

import ru.codeportfolio.tasktracker.model.Status;

import java.sql.Timestamp;

public record TaskDto(
        Long id,
        String name,
        String text,
        UserDto owner,
        Status status,
        Timestamp timestamp

) {
}

package ru.codeportfolio.tasktracker.dto;

import ru.codeportfolio.tasktracker.model.Status;

import java.sql.Timestamp;
import java.util.UUID;

public record TaskDto(
        Long id,
        String name,
        String text,
        UserDto owner,
        Status status,
        Timestamp timestamp

) {
}

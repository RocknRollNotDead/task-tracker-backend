package ru.codeportfolio.tasktracker.util;

import ru.codeportfolio.tasktracker.dto.UserDto;
import ru.codeportfolio.tasktracker.model.User;

public final class UserMapper {
    private UserMapper() {

    }

    public static UserDto execute(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}

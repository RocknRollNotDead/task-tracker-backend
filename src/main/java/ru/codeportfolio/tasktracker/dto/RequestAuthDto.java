package ru.codeportfolio.tasktracker.dto;

import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.codeportfolio.tasktracker.config.TrimStringDeserializer;

public record RequestAuthDto(
        @NotBlank(message = "Username can't be empty!")
        @JsonDeserialize(using = TrimStringDeserializer.class)
        String username,

        @NotBlank(message = "Password can't be empty!")
        @Size(min = RequestAuthDto.PASSWORD_MIN_LENGTH,
                message = "Password must be at least {min} characters")
        @Pattern(regexp = "^\\S+$", message = "Password can't contain whitespace")
        String password,

        @NotBlank(message = "Email cant be empty!")
        @Email(message = "Invalid email!")
        @JsonDeserialize(using = TrimStringDeserializer.class)
        String email
) {
        private static final int PASSWORD_MIN_LENGTH = 5;
}

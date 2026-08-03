package ru.codeportfolio.tasktracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.codeportfolio.tasktracker.config.TrimStringDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

public record RequestRegistrationDto(
        @NotBlank(message = "Username can't be empty!")
        @JsonDeserialize(using = TrimStringDeserializer.class)
        String username,

        @NotBlank(message = "Email cant be empty!")
        @Email(message = "Invalid email!")
        @JsonDeserialize(using = TrimStringDeserializer.class)
        String email,

        @NotBlank(message = "Password can't be empty!")
        @Size(min = RequestRegistrationDto.PASSWORD_MIN_LENGTH,
                message = "Password must be at least {min} characters")
        @Pattern(regexp = "^\\S+$", message = "Password can't contain whitespace")
        String password

) {
    private static final int PASSWORD_MIN_LENGTH = 5;
}

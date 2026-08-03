package ru.codeportfolio.tasktracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import ru.codeportfolio.tasktracker.config.TrimStringDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

public record RequestAuthDto(

        @NotBlank(message = "Email cant be empty!")
        @Email(message = "Invalid email!")
        @JsonDeserialize(using = TrimStringDeserializer.class)
        String email,

        @NotBlank(message = "Password can't be empty!")
        @Pattern(regexp = "^\\S+$", message = "Password can't contain whitespace")
        String password

) {

}


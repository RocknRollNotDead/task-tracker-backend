package ru.codeportfolio.tasktracker.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "secrets.admin")
public record AdminProperties(
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotBlank
        String email
) {
}


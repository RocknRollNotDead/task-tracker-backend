package ru.codeportfolio.tasktracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "secrets.admin")
public record AdminProperties(
        String username,
        String password,
        String email
) {
}


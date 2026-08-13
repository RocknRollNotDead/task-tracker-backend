package ru.codeportfolio.tasktracker.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
        @NotBlank
        String url,

        @NotBlank
        String producerName
) {
}


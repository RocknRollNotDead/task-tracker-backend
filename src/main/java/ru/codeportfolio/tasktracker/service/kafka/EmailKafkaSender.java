package ru.codeportfolio.tasktracker.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.codeportfolio.tasktracker.dto.EmailDto;
import tools.jackson.databind.ObjectMapper;

@Service
public class EmailKafkaSender {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EmailKafkaSender(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void executeSend(EmailDto emailDto) {
        String json = objectMapper.writeValueAsString(emailDto);
        kafkaTemplate.send("EMAIL_SENDING_TASKS", json);
    }
}

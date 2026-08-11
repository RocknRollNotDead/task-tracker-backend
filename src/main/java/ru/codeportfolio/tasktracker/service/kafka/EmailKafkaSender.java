package ru.codeportfolio.tasktracker.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.codeportfolio.tasktracker.dto.EmailDto;
import tools.jackson.databind.ObjectMapper;

@Slf4j
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
        kafkaTemplate.send("EMAIL_SENDING_TASKS", json)
                .whenComplete((result, e) ->
                {
                    if (e != null) {
                        log.error("Error to send to kafka to mail for address {}", emailDto.email());
                    }
                });
    }
}

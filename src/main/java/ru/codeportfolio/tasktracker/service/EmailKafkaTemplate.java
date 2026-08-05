package ru.codeportfolio.tasktracker.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailKafkaTemplate {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public EmailKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(String string){
        kafkaTemplate.send("EMAIL_SENDING_TASKS", string);
    }
}

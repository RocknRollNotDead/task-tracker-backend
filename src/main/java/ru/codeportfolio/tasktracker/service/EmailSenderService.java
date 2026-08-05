package ru.codeportfolio.tasktracker.service;

import org.springframework.stereotype.Service;
import ru.codeportfolio.tasktracker.dto.EmailDto;
import tools.jackson.databind.ObjectMapper;

@Service
public class EmailSenderService {

    public final static String TEXT_WELCOME_MAIL_MUST_BE_FORMATTED = """
            
            Приветствуем, %s!
            Вы зарегистрировались в нашем сервисе Task Ledger на сайте %s!
            
            Приятного пользования!
            
            """;
    public final static String DOMAIN = "codeportfolio.ru";
    private final static String HEADER = "Приветственное письмо от Task Ledger";

    private final EmailKafkaTemplate emailKafkaTemplate;
    private final ObjectMapper objectMapper;

    public EmailSenderService(EmailKafkaTemplate emailKafkaTemplate, ObjectMapper objectMapper) {
        this.emailKafkaTemplate = emailKafkaTemplate;
        this.objectMapper = objectMapper;
    }


    public void sendWelcomeEmail(String email, String username) {

        EmailDto emailDto = new EmailDto(
                email,
                HEADER,
                TEXT_WELCOME_MAIL_MUST_BE_FORMATTED.formatted(username, DOMAIN)
        );
        String json = objectMapper.writeValueAsString(emailDto);
        emailKafkaTemplate.sendOrder(json);

    }
}

package ru.codeportfolio.tasktracker.service;

import org.springframework.stereotype.Service;
import ru.codeportfolio.tasktracker.dto.EmailDto;
import ru.codeportfolio.tasktracker.service.kafka.EmailKafkaSender;

@Service
public class EmailSenderService {

    public final static String TEXT_WELCOME_MAIL_MUST_BE_FORMATTED = """
            
            Приветствуем, %s!
            Вы зарегистрировались в нашем сервисе Task Ledger на сайте %s!
            
            Приятного пользования!
            
            """;
    public final static String DOMAIN = "codeportfolio.ru";
    private final static String HEADER = "Приветственное письмо от Task Ledger";

    private final EmailKafkaSender emailKafkaSender;

    public EmailSenderService(EmailKafkaSender emailKafkaSender) {
        this.emailKafkaSender = emailKafkaSender;
    }


    public void sendWelcomeEmail(String email, String username) {

        EmailDto emailDto = new EmailDto(
                email,
                HEADER,
                TEXT_WELCOME_MAIL_MUST_BE_FORMATTED.formatted(username, DOMAIN)
        );

        emailKafkaSender.executeSend(emailDto);

    }
}

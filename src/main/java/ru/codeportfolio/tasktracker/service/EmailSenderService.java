package ru.codeportfolio.tasktracker.service;

import org.springframework.stereotype.Service;
import ru.codeportfolio.tasktracker.dto.EmailDto;
import ru.codeportfolio.tasktracker.service.kafka.EmailKafkaSender;
import ru.codeportfolio.tasktracker.util.EmailUtil;

@Service
public class EmailSenderService {

    private final EmailKafkaSender emailKafkaSender;

    public EmailSenderService(EmailKafkaSender emailKafkaSender) {
        this.emailKafkaSender = emailKafkaSender;
    }


    public void sendWelcomeEmail(String email, String username) {

        EmailDto emailDto = new EmailDto(
                email,
                EmailUtil.HEADER,
                EmailUtil.getWelcomeText(username)
        );

        emailKafkaSender.executeSend(emailDto);

    }
}

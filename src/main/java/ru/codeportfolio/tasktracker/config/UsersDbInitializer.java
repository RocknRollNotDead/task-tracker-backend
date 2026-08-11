package ru.codeportfolio.tasktracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.model.Role;
import ru.codeportfolio.tasktracker.model.User;


@Component
public class UsersDbInitializer implements CommandLineRunner {

    public static final String EMAIL_ADMIN = "1@a.ru";
    public static final String EMAIL_USER = "2@a.ru";


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${secrets.admin-login}")
    private String usernameAdmin;
    @Value("${secrets.admin-password}")
    private String passwordAdmin;

    @Value("${secrets.user-login}")
    private String usernameUser;
    @Value("${secrets.user-password}")
    private String passwordUser;

    public UsersDbInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findUsersByEmail(EMAIL_ADMIN).isEmpty()) {
            userRepository.save(new User(
                            usernameAdmin,
                            passwordEncoder.encode(passwordAdmin),
                            Role.ADMIN,
                            EMAIL_ADMIN
                    )
            );
        }
        if (userRepository.findUsersByEmail(EMAIL_USER).isEmpty()) {
            userRepository.save(new User(
                            usernameUser,
                            passwordEncoder.encode(passwordUser),
                            Role.USER,
                            EMAIL_USER
                    )
            );
        }


    }


}

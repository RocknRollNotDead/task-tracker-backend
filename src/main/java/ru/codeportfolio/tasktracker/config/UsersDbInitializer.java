package ru.codeportfolio.tasktracker.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.model.Role;
import ru.codeportfolio.tasktracker.model.User;


@Component
public class UsersDbInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String usernameAdmin;
    private final String passwordAdmin;
    private final String usernameUser;
    private final String passwordUser;
    public final String emailAdmin;
    public final String emailUser;


    public UsersDbInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                              UserProperties userProperties, AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        this.usernameAdmin = adminProperties.username();
        this.passwordAdmin = adminProperties.password();
        this.emailAdmin = adminProperties.email();

        this.usernameUser = userProperties.username();
        this.passwordUser = userProperties.password();
        this.emailUser = userProperties.email();
    }

    @Override
    public void run(String... args) {

        if (userRepository.findUsersByEmail(emailAdmin).isEmpty()) {
            userRepository.save(new User(
                            usernameAdmin,
                            passwordEncoder.encode(passwordAdmin),
                            Role.ADMIN,
                    emailAdmin
                    )
            );
        }
        if (userRepository.findUsersByEmail(emailUser).isEmpty()) {
            userRepository.save(new User(
                            usernameUser,
                            passwordEncoder.encode(passwordUser),
                            Role.USER,
                    emailUser
                    )
            );
        }


    }


}

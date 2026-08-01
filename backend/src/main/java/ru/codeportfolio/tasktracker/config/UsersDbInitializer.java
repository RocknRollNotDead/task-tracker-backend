package ru.codeportfolio.tasktracker.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.codeportfolio.tasktracker.dao.TaskRepository;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.model.Role;
import ru.codeportfolio.tasktracker.model.User;


@Component
public class UsersDbInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;

    public UsersDbInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {


        String username = System.getenv("DB_LOGIN");
        if (userRepository.findUsersByEmail("1@a.ru").isEmpty()) {
            userRepository.save(new User(
                            username,
                            passwordEncoder.encode(System.getenv("DB_PASSWORD")),
                            Role.ADMIN,
                            "1@a.ru"
                    )
            );
        }
        String email = System.getenv("EMAIL_USER");
        if (userRepository.findUsersByEmail(email).isEmpty()) {
            System.out.println(email);
            userRepository.save(new User(
                            "User",
                            passwordEncoder.encode(System.getenv("PASSWORD_USER")),
                            Role.USER,
                            email
                    )
            );
        }


    }


}

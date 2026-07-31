package ru.codeportfolio.tasktracker.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.dto.UserDto;
import ru.codeportfolio.tasktracker.exception.AlreadyExistException;
import ru.codeportfolio.tasktracker.exception.NotFoundException;
import ru.codeportfolio.tasktracker.model.Role;
import ru.codeportfolio.tasktracker.model.User;
import ru.codeportfolio.tasktracker.util.Validator;


@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserDto createUser(String username, String password, String email) {

        username = Validator.validateUsername(username);
        password = Validator.validatePasswordWithLength(password);

        password = passwordEncoder.encode(password);
        User user;
        try {
            user = userRepository.save(new User(username, password, Role.USER, email));
        } catch (DataIntegrityViolationException e) {

            throw new AlreadyExistException("Username %s already exist.".formatted(username));
        }

        return new UserDto(user.getLogin());
    }

    public UserDto getInfo(String username) {

        Validator.validateUsername(username);

        User user = userRepository.findUsersByLogin(username).orElseThrow(
                () -> new NotFoundException("user %s not found".formatted(username)));

        return new UserDto(user.getLogin());
    }

}

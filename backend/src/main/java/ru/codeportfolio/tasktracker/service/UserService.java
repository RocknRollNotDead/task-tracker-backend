package ru.codeportfolio.tasktracker.service;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.dto.RequestAuthDto;
import ru.codeportfolio.tasktracker.dto.UserDto;
import ru.codeportfolio.tasktracker.exception.entity.AlreadyExistException;
import ru.codeportfolio.tasktracker.exception.entity.NotFoundException;
import ru.codeportfolio.tasktracker.model.Role;
import ru.codeportfolio.tasktracker.model.User;
import ru.codeportfolio.tasktracker.util.UserMapper;


@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailSenderService emailSenderService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
    }


    public UserDto createUser(RequestAuthDto dto) {


        String password = passwordEncoder.encode(dto.password());
        User user = null;
        try {
            user = userRepository.save(new User(
                    dto.username(),
                    password,
                    Role.USER,
                    dto.email()));
        } catch (DataIntegrityViolationException e) {
            throwSaveException(dto, e);
        }

        // делегирование
        emailSenderService.sendWelcomeEmail(dto.email());

        return UserMapper.execute(user);
    }

    public UserDto getInfo(Long id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found in db"));

        return new UserDto(user.getId(), user.getEmail());
    }

    private static void throwSaveException(RequestAuthDto dto, DataIntegrityViolationException e) {
        Throwable rootCause = e.getMostSpecificCause();

        if (rootCause instanceof PSQLException psqlEx) {
            String sqlState = psqlEx.getSQLState();
            String message = psqlEx.getServerErrorMessage() != null
                    ? psqlEx.getServerErrorMessage().getConstraint()
                    : null;

            if ("23505".equals(sqlState)) {
                if (message != null && message.contains("email")) {
                    throw new AlreadyExistException("Email %s already exist.".formatted(dto.email()));
                }
            }
        }
    }


}

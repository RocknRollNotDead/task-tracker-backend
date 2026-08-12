package ru.codeportfolio.tasktracker.service;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.dto.http.request.RequestRegistrationDto;
import ru.codeportfolio.tasktracker.exception.entity.AlreadyExistException;
import ru.codeportfolio.tasktracker.model.Role;
import ru.codeportfolio.tasktracker.model.User;

@Slf4j
@Service
public class UserSaverService {
    private final UserRepository userRepository;

    public UserSaverService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User saveUserToRepository(RequestRegistrationDto dto, String password) {
        try {
            return userRepository.save(new User(
                    dto.username(),
                    password,
                    Role.USER,
                    dto.email()));
        } catch (DataIntegrityViolationException e) {
            throwSaveException(dto, e);
            log.error(e.getMessage());
            throw e;
        }
    }

    private static void throwSaveException(RequestRegistrationDto dto, DataIntegrityViolationException e) {
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
        throw e;
    }

}

package ru.codeportfolio.tasktracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.dto.http.request.RequestRegistrationDto;
import ru.codeportfolio.tasktracker.dto.http.response.UserDto;
import ru.codeportfolio.tasktracker.exception.entity.NotFoundException;
import ru.codeportfolio.tasktracker.model.User;
import ru.codeportfolio.tasktracker.util.UserMapper;


@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final UserSaverService userSaverService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailSenderService emailSenderService, UserSaverService userSaverService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
        this.userSaverService = userSaverService;
    }


    public UserDto createUser(RequestRegistrationDto dto) {


        String password = passwordEncoder.encode(dto.password());
        User user = userSaverService.saveUserToRepository(dto, password);

        try {
            emailSenderService.sendWelcomeEmail(dto.email(), dto.username());
        } catch (Exception e) {
            log.warn("Email send to {} error!", dto.email());
        }

        log.info("Registration user {} with mail {}", dto.username(), dto.email());

        return UserMapper.execute(user);
    }

    @Transactional(readOnly = true)
    public UserDto getInfo(Long id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found in db"));

        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }


}

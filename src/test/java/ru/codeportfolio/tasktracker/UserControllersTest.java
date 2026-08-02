package ru.codeportfolio.tasktracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.codeportfolio.tasktracker.dao.TaskRepository;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.model.CustomUserDetails;
import ru.codeportfolio.tasktracker.model.Role;
import ru.codeportfolio.tasktracker.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserControllersTest extends IntegrationTestBase {


    public static final String API_LOGIN = "/auth/login";
    public static final String API_SIGN_OUT = "/auth/sign-out";
    public static final String API_USER = "/user";


    private final static String EMAIL_TEST_USER = "4@a.ru";
    private final static String PASSWORD_TEST_USER = "password";
    private final static String USERNAME_TEST_USER = "test-user";

    private UsernamePasswordAuthenticationToken token;


    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    private final String json = """
            {"username": "%s", "email": "%s", "password": "%s"}
            """.formatted(USERNAME_TEST_USER, EMAIL_TEST_USER, PASSWORD_TEST_USER);
    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void clean() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.flush();

    }

    void createTestUser() {
        User user = userRepository.findUsersByEmail(EMAIL_TEST_USER)
                .orElseGet(() -> userRepository.save(new User(
                        USERNAME_TEST_USER,
                        passwordEncoder.encode(PASSWORD_TEST_USER),
                        Role.USER,
                        EMAIL_TEST_USER
                )));

        token = getAuth(user.getId());
    }


    @Test
    void shouldCreateUser() throws Exception {


        mockMvc.perform(post(API_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(USERNAME_TEST_USER));

        assertThat(userRepository.findUsersByEmail(EMAIL_TEST_USER)).isPresent();
    }

    @Test
    void shouldNotCreateUser() throws Exception {


        mockMvc.perform(post(API_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").exists());

        mockMvc.perform(post(API_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());

        assertThat(userRepository.findUsersByEmail(EMAIL_TEST_USER)).isPresent();
    }


    @Test
    void shouldGetUserInfo() throws Exception {
        createTestUser();

        mockMvc.perform(get(API_USER)
                        .with(authentication(token))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME_TEST_USER));

        assertThat(userRepository.findUsersByEmail(EMAIL_TEST_USER)).isPresent();
    }

    @Test
    void shouldReturn401WhenUserNotAuth() throws Exception {
        mockMvc.perform(get(API_USER))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    void shouldLogIn() throws Exception {
        mockMvc.perform(post(API_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        mockMvc.perform(post(API_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME_TEST_USER));
    }

    @Test
    void shouldNotLogIn() throws Exception {

        mockMvc.perform(post(API_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void shouldLogout() throws Exception {
        createTestUser();
        mockMvc.perform(post(API_SIGN_OUT)
                        .with(authentication(token)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotLogout() throws Exception {
        mockMvc.perform(post(API_SIGN_OUT))
                .andExpect(status().isUnauthorized());
    }

    private UsernamePasswordAuthenticationToken getAuth(long id) {

        CustomUserDetails userDetails = new CustomUserDetails(
                new User(id, USERNAME_TEST_USER, PASSWORD_TEST_USER, Role.USER, EMAIL_TEST_USER)
        );
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
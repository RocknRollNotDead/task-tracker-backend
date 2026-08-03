package ru.codeportfolio.tasktracker;


import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.codeportfolio.tasktracker.dao.TaskRepository;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.model.*;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
class TasksApiTest extends IntegrationTestBase {

    public static final String API_TASKS = "/tasks";


    private final static String EMAIL_TEST_USER = "4@a.ru";
    private final static String PASSWORD_TEST_USER = "password";
    private final static String USERNAME_TEST_USER = "test-user";
    public static final String TEST_TASK_NAME = "Test task";
    public static final String TEST_TASK_TEXT = "Test task text";
    private UsernamePasswordAuthenticationToken token;
    public static final String REQUEST_BODY_JSON = """
            {
                "name": "%s",
                "text": "%s"
            }
            """;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;


    @BeforeEach
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

    @BeforeEach
    void cleanTasks() {
        taskRepository.deleteAll();
    }

    @BeforeAll
    static void setUp() {

    }


    @Test
    void shouldCreateTask() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.owner.username").value(USERNAME_TEST_USER))
                .andExpect(jsonPath("$.name").value(TEST_TASK_NAME))
                .andExpect(jsonPath("$.text").value(TEST_TASK_TEXT))
        ;

    }


    @Test
    void notShouldCreateTask() throws Exception {
        createTask("", TEST_TASK_TEXT)
                .andExpect(status().isBadRequest())
                .andExpect(checkExistErrorMessage())
        ;

    }


    @Test
    void notShouldCreateTask_unauth() throws Exception {
        mockMvc.perform(post(API_TASKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_BODY_JSON.formatted(TEST_TASK_NAME, TEST_TASK_TEXT)))
                .andExpect(status().isUnauthorized())
                .andExpect(checkExistErrorMessage())
        ;

    }

    @Test
    void shouldGetTasks() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(get(API_TASKS)
                        .with(authentication(token))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(TEST_TASK_NAME));
    }

    @Test
    void shouldGetEmptyTasks() throws Exception {

        mockMvc.perform(get(API_TASKS)
                        .with(authentication(token))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldExecuteTask() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        changeStatusTask();

        expectTaskWithStatus(Status.DONE);

    }

    @Test
    void shouldReturnExecuteTask() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        changeStatusTask();

        expectTaskWithStatus(Status.DONE);

        changeStatusTask();

        expectTaskWithStatus(Status.IN_PROGRESS);
    }

    @Test
    public void notChangeStatusTask() throws Exception {
        mockMvc.perform(patch(API_TASKS)
                        .with(authentication(token))
                        .param("taskId", "234")
                )
                .andExpect(status().isNotFound())
                .andExpect(checkExistErrorMessage())
        ;
    }

    @Test
    public void notChangeStatusTaskNotConvertId() throws Exception {
        mockMvc.perform(patch(API_TASKS)
                        .with(authentication(token))
                        .param("taskId", "ыыыыыыыЫ")
                )
                .andExpect(status().isBadRequest())
                .andExpect(checkExistErrorMessage())
        ;
    }

    @Test
    public void notChangeStatusTask_unauth() throws Exception {
        mockMvc.perform(patch(API_TASKS)
                        .param("taskId", getTaskId())
                )
                .andExpect(status().isUnauthorized())
                .andExpect(checkExistErrorMessage())
        ;
    }


    @Test
    public void editTask() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(patch(API_TASKS + "/edit")
                        .with(authentication(token))
                        .param("taskId", getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_BODY_JSON.formatted("edited task", "edited text task"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("edited task"))
                .andExpect(jsonPath("$.owner.username").value(USERNAME_TEST_USER))
                .andExpect(jsonPath("$.text").value("edited text task"))
        ;

    }

    @Test
    public void notEditTaskBadId() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(patch(API_TASKS + "/edit")
                        .with(authentication(token))
                        .param("taskId", "1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_BODY_JSON.formatted("edited task", "edited text task"))
                )
                .andExpect(status().isNotFound());
    }


    @Test
    public void notEditTaskNoName() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(patch(API_TASKS + "/edit")
                        .with(authentication(token))
                        .param("taskId", getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_BODY_JSON.formatted("", "edited text task"))
                )
                .andExpect(status().isBadRequest())
                .andExpect(checkExistErrorMessage());
    }


    @Test
    public void notEditTaskBlankName() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(patch(API_TASKS + "/edit")
                        .with(authentication(token))
                        .param("taskId", getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_BODY_JSON.formatted("          ", "edited text task"))
                )
                .andExpect(status().isBadRequest())
                .andExpect(checkExistErrorMessage());
    }

    @Test
    public void editTaskNoText() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(patch(API_TASKS + "/edit")
                        .with(authentication(token))
                        .param("taskId", getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_BODY_JSON.formatted("name req", ""))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name req"))
                .andExpect(jsonPath("$.owner.username").value(USERNAME_TEST_USER))
                .andExpect(jsonPath("$.text").value(""))
        ;
    }

    @Test
    public void noEditTaskNoAuth() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(patch(API_TASKS + "/edit")
                        .param("taskId", getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_BODY_JSON.formatted("name req", ""))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(checkExistErrorMessage())
        ;
    }


    @Test
    public void deleteTask() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(delete(API_TASKS)
                        .with(authentication(token))
                        .param("taskId", getTaskId())
                )
                .andExpect(status().isNoContent())
        ;

        mockMvc.perform(get(API_TASKS)
                        .with(authentication(token))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
        ;

    }


    @Test
    public void notDeleteTaskNotFound() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(delete(API_TASKS)
                        .with(authentication(token))
                        .param("taskId", "12344")
                )
                .andExpect(status().isNotFound())
                .andExpect(checkExistErrorMessage())
        ;
    }


    @Test
    public void notDeleteTaskUnauthorized() throws Exception {
        createTask(TEST_TASK_NAME, TEST_TASK_TEXT)
                .andExpect(status().isCreated());

        mockMvc.perform(delete(API_TASKS)
                        .param("taskId", "12344")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(checkExistErrorMessage())
        ;
    }


    private static @NonNull ResultMatcher checkExistErrorMessage() {
        return jsonPath("$.message").exists();
    }


    private void expectTaskWithStatus(Status status) throws Exception {
        mockMvc.perform(get(API_TASKS)
                        .with(authentication(token))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(TEST_TASK_NAME))
                .andExpect(jsonPath("$[0].status").value(status.toString()))
        ;
    }

    private void changeStatusTask() throws Exception {
        mockMvc.perform(patch(API_TASKS)
                        .with(authentication(token))
                        .param("taskId", getTaskId())
                )
                .andExpect(status().isOk());
    }

    private String getTaskId() {


        List<Task> tasks = taskRepository.getTasksByOwner_Id(
                userRepository.findUsersByEmail(EMAIL_TEST_USER).orElseThrow()
                        .getId());

        if (tasks.isEmpty()) {
            return null;
        }
        return String.valueOf(tasks

                .getFirst().getId());
    }

    private @NonNull ResultActions createTask(String taskName, String text) throws Exception {
        return mockMvc.perform(post(API_TASKS)
                .with(authentication(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_BODY_JSON.formatted(taskName, text)));
    }


    private UsernamePasswordAuthenticationToken getAuth(long id) {

        CustomUserDetails userDetails = new CustomUserDetails(
                new User(id, USERNAME_TEST_USER, PASSWORD_TEST_USER, Role.USER, EMAIL_TEST_USER)
        );
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }


}
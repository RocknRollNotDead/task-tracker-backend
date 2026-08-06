package ru.codeportfolio.tasktracker.service;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codeportfolio.tasktracker.dao.TaskRepository;
import ru.codeportfolio.tasktracker.dao.UserRepository;
import ru.codeportfolio.tasktracker.dto.http.request.RequestTaskDto;
import ru.codeportfolio.tasktracker.dto.http.response.TaskDto;
import ru.codeportfolio.tasktracker.exception.entity.NotFoundException;
import ru.codeportfolio.tasktracker.model.Task;
import ru.codeportfolio.tasktracker.model.User;
import ru.codeportfolio.tasktracker.util.TaskMapper;

import java.util.Comparator;
import java.util.List;

@Transactional
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskDto create(Long userId, RequestTaskDto taskDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User from session not found!"));

        Task task = taskRepository.save(new Task(
                taskDto.name(),
                taskDto.text(),
                owner
        ));

        return TaskMapper.execute(task);
    }

    public List<TaskDto> get(Long userId) {
        List<Task> tasks = taskRepository.getTasksByOwner_Id(userId);
        tasks.sort(Comparator.comparing(
                Task::getTimestamp,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return TaskMapper.MapList(tasks);
    }


    public TaskDto patch(Long userId, Long taskId) {
        Task task = getUserTask(userId, taskId);
        task.setStatus();
        return TaskMapper.execute(taskRepository.save(task));
    }

    public void delete(Long userId, Long taskId) {
        Task task = getUserTask(userId, taskId);
        taskRepository.delete(task);
    }


    public TaskDto edit(Long userId, Long taskId, RequestTaskDto dto) {
        Task task = getUserTask(userId, taskId);
        task.setName(dto.name());
        task.setText(dto.text());
        return TaskMapper.execute(taskRepository.save(task));

    }

    private @NonNull Task getUserTask(Long userId, Long taskId) {
        Task task = taskRepository.getTaskById(taskId);
        if (task == null || !task.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Not found in your task!");
        }
        return task;
    }


}

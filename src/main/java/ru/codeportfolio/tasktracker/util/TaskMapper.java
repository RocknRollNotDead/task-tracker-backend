package ru.codeportfolio.tasktracker.util;

import ru.codeportfolio.tasktracker.dto.response.TaskDto;
import ru.codeportfolio.tasktracker.model.Task;

import java.util.List;

public final class TaskMapper {
    private TaskMapper() {
    }

    public static TaskDto execute(Task task) {

        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getText(),
                UserMapper.execute(task.getOwner()),
                task.getStatus(),
                task.getTimestamp());
    }

    public static List<TaskDto> MapList(List<Task> tasks) {
        return tasks.stream().map(TaskMapper::execute).toList();
    }

}

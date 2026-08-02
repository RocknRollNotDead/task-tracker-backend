package ru.codeportfolio.tasktracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.codeportfolio.tasktracker.model.Task;
import ru.codeportfolio.tasktracker.model.User;

import java.util.List;

@Repository
public interface TaskRepository  extends JpaRepository<Task, Long> {
    List<Task> getTasksByOwner_Id(Long ownerId);

    Task getTaskById(Long id);
}

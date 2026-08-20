package ru.codeportfolio.tasktracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.codeportfolio.tasktracker.model.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByOwner_Id(Long ownerId);

    Optional<Task> findByIdAndOwnerId(Long taskId, Long userId);

    List<Task> findByOwnerIdOrderByTimestampDesc(Long ownerId);
}

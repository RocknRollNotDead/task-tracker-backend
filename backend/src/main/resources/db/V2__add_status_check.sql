UPDATE tasks SET timestamp = NULL WHERE status <> 1;

ALTER TABLE tasks
    ADD CONSTRAINT chk_tasks_status_timestamp
    CHECK (
        (status = 1 AND timestamp IS NOT NULL) OR
        (status = 0 AND timestamp IS NULL)
    );
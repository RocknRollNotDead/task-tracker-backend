import { useCallback, useEffect, useState } from "react";
import {
  fetchTasks,
  createTask,
  toggleTaskStatus,
  editTask,
  deleteTask,
} from "../api/tasksApi";

const STATUS = (window.APP_CONFIG || {}).TASK_STATUS || {
  DONE: "DONE",
  NOT_DONE: "NOT_DONE",
};

export function isDone(task) {
  return task.status === STATUS.DONE;
}

export function useTasks(isAuthenticated) {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const list = await fetchTasks();
      setTasks(list || []);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      load();
    } else {
      setTasks([]);
    }
  }, [isAuthenticated, load]);

  const addTask = useCallback(async (name, text = "") => {
    const created = await createTask({ name, text });
    setTasks((prev) => [created, ...prev]);
    return created;
  }, []);

  const toggleTask = useCallback(async (taskId) => {
    const updated = await toggleTaskStatus(taskId);
    setTasks((prev) => prev.map((t) => (t.id === taskId ? updated : t)));
    return updated;
  }, []);

  // Немедленно обновляет задачу локально (для отзывчивого UI при наборе текста),
  // не дожидаясь ответа сервера.
  const patchLocal = useCallback((taskId, patch) => {
    setTasks((prev) => prev.map((t) => (t.id === taskId ? { ...t, ...patch } : t)));
  }, []);

  const persistEdit = useCallback(async (taskId, { name, text }) => {
    const updated = await editTask(taskId, { name, text });
    setTasks((prev) => prev.map((t) => (t.id === taskId ? updated : t)));
    return updated;
  }, []);

  const removeTask = useCallback(async (taskId) => {
    await deleteTask(taskId);
    setTasks((prev) => prev.filter((t) => t.id !== taskId));
  }, []);

  return {
    tasks,
    loading,
    error,
    reload: load,
    addTask,
    toggleTask,
    patchLocal,
    persistEdit,
    removeTask,
  };
}

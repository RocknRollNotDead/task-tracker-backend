import { request } from "./httpClient";

const EP = (window.APP_CONFIG || {}).ENDPOINTS || {};

export function fetchTasks() {
  return request(EP.TASKS || "/tasks", { method: "GET" });
}

export function createTask({ name, text }) {
  return request(EP.TASKS || "/tasks", {
    method: "POST",
    body: { name, text: text || "" },
  });
}

/** Переключает статус задачи (сделано / не сделано). Тело не требуется. */
export function toggleTaskStatus(taskId) {
  return request(EP.TASKS || "/tasks", {
    method: "PATCH",
    params: { taskId },
  });
}

/**
 * Контроллер: PATCH /tasks/edit?taskId=... c @RequestBody RequestTaskDto dto.
 * taskId — query-параметр, name/text — JSON body.
 */
export function editTask(taskId, { name, text }) {
  return request(EP.TASK_EDIT || "/tasks/edit", {
    method: "PATCH",
    params: { taskId },
    body: { name, text: text || "" },
  });
}

export function deleteTask(taskId) {
  return request(EP.TASKS || "/tasks", {
    method: "DELETE",
    params: { taskId },
  });
}

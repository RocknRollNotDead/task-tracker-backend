const cfg = window.APP_CONFIG || {};
const BASE = (cfg.API_BASE_URL || "") + (cfg.API_PREFIX || "");

export class ApiError extends Error {
  constructor(message, status, fieldErrors) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    // { fieldName: "текст ошибки" } — для отрисовки под конкретными полями формы
    this.fieldErrors = fieldErrors || null;
  }
}

/**
 * Универсальный запрос к API.
 * @param {string} path - путь эндпоинта (см. window.APP_CONFIG.ENDPOINTS)
 * @param {object} options
 * @param {"GET"|"POST"|"PATCH"|"DELETE"} [options.method]
 * @param {object} [options.body] - будет сериализован в JSON
 * @param {object} [options.params] - query-параметры
 */
export async function request(path, { method = "GET", body, params } = {}) {
  let url = BASE + path;

  if (params) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) query.append(key, value);
    });
    const qs = query.toString();
    if (qs) url += "?" + qs;
  }

  let response;
  try {
    response = await fetch(url, {
      method,
      credentials: cfg.WITH_CREDENTIALS === false ? "same-origin" : "include",
      headers: body !== undefined ? { "Content-Type": "application/json" } : undefined,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch (networkError) {
    throw new ApiError("Не удалось связаться с сервером. Проверьте подключение.", 0, null);
  }

  if (response.status === 204) {
    return null;
  }

  const text = await response.text();
  let data = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = null;
    }
  }

  if (!response.ok) {
    throw new ApiError(
      extractMessage(data, response.status),
      response.status,
      extractFieldErrors(data)
    );
  }

  return data;
}

function extractMessage(data, status) {
  if (data) {
    if (typeof data.message === "string" && data.message.trim()) return data.message;
    if (typeof data.error === "string" && data.error.trim()) return data.error;
  }
  if (status === 401) return "Неверный email или пароль.";
  if (status === 409) return "Такой пользователь уже существует.";
  if (status === 403) return "Доступ запрещён.";
  if (status === 404) return "Не найдено.";
  return "Что-то пошло не так. Попробуйте ещё раз.";
}

function extractFieldErrors(data) {
  if (!data) return null;

  // Формат вида { "email": "Invalid email!", "password": "..." }
  if (data.errors && !Array.isArray(data.errors) && typeof data.errors === "object") {
    return data.errors;
  }
  // Формат вида { fieldErrors: { email: "..." } }
  if (data.fieldErrors && typeof data.fieldErrors === "object") {
    return data.fieldErrors;
  }
  // Стандартный список Spring: [{ field, defaultMessage }]
  if (Array.isArray(data.errors)) {
    const collected = {};
    data.errors.forEach((entry) => {
      const field = entry.field || entry.objectName;
      const msg = entry.defaultMessage || entry.message;
      if (field && msg) collected[field] = msg;
    });
    return Object.keys(collected).length ? collected : null;
  }
  return null;
}

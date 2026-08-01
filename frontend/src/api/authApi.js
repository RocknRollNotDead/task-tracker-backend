import { request } from "./httpClient";

const EP = (window.APP_CONFIG || {}).ENDPOINTS || {};

/**
 * Форма входа в этом приложении содержит только email и пароль (username там
 * не спрашивается), а RequestAuthDto на бэкенде требует username всегда.
 * Поэтому при логине username по-прежнему прозрачно = email.
 */
export function login({ email, password }) {
  return request(EP.LOGIN || "/auth/login", {
    method: "POST",
    body: { email: email, password },
  });
}

/**
 * Форма регистрации теперь содержит настоящее поле username — оно
 * отправляется как есть, отдельно от email.
 *
 * ВАЖНО: UserDto на бэкенде сейчас возвращает только { id, email } —
 * username в ответе сервера нет. Чтобы дальше "обращаться к пользователю
 * по username" (в шапке, и т.д.), на бэкенде нужно добавить поле username
 * в UserDto (и в UserService.createUser/getInfo, откуда он собирается).
 * До этого фронтенд просто откатывается на email везде, где username
 * не пришёл (см. Header.jsx).
 */
export function register({ username, email, password }) {
  return request(EP.REGISTER || "/user", {
    method: "POST",
    body: { username, email, password },
  });
}

export function logout() {
  return request(EP.LOGOUT || "/auth/sign-out", { method: "POST" });
}

export function fetchCurrentUser() {
  return request(EP.CURRENT_USER || "/user", { method: "GET" });
}

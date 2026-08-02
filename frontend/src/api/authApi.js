import { request } from "./httpClient";

const EP = (window.APP_CONFIG || {}).ENDPOINTS || {};


export function login({ email, password }) {
  return request(EP.LOGIN || "/auth/login", {
    method: "POST",
    body: { email: email, password },
  });
}


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

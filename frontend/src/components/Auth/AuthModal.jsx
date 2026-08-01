import { useState } from "react";
import Modal from "../common/Modal.jsx";
import { useAuth } from "../../context/AuthContext.jsx";
import { ApiError } from "../../api/httpClient.js";
import "./AuthModal.css";

const MODES = {
  login: {
    title: "Вход",
    eyebrow: "С возвращением",
    submitLabel: "Войти",
    switchHint: "Ещё нет аккаунта?",
    switchLabel: "Зарегистрироваться",
  },
  register: {
    title: "Регистрация",
    eyebrow: "Новый аккаунт",
    submitLabel: "Создать аккаунт",
    switchHint: "Уже есть аккаунт?",
    switchLabel: "Войти",
  },
};

export default function AuthModal({ mode, onClose, onSwitchMode }) {
  const { login, register } = useAuth();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [formError, setFormError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const copy = MODES[mode];
  const isRegister = mode === "register";

  function clearFieldError(field) {
    setFieldErrors((prev) => {
      if (!prev[field]) return prev;
      const next = { ...prev };
      delete next[field];
      return next;
    });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setFormError(null);

    // Клиентская валидация "как в Thymeleaf" — до похода на сервер.
    const nextErrors = {};
    if (isRegister && !username.trim()) nextErrors.username = "Имя пользователя не может быть пустым";
    if (!email.trim()) nextErrors.email = "Email не может быть пустым";
    if (!password) nextErrors.password = "Пароль не может быть пустым";
    if (isRegister) {
      if (!repeatPassword) nextErrors.repeatPassword = "Повторите пароль";
      else if (repeatPassword !== password) nextErrors.repeatPassword = "Пароли не совпадают";
    }
    if (Object.keys(nextErrors).length) {
      setFieldErrors(nextErrors);
      return;
    }

    setSubmitting(true);
    try {
      if (isRegister) {
        await register({ username, email, password });
      } else {
        await login({ email, password });
      }
      onClose();
    } catch (err) {
      if (err instanceof ApiError) {
        setFormError(err.fieldErrors ? null : err.message);
        if (err.fieldErrors) setFieldErrors(err.fieldErrors);
      } else {
        setFormError("Что-то пошло не так. Попробуйте ещё раз.");
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Modal title={copy.title} eyebrow={copy.eyebrow} onClose={onClose} width="sm">
      <form onSubmit={handleSubmit} noValidate>
        {formError ? <div className="form-banner">{formError}</div> : null}

        {isRegister ? (
          <div className={`field ${fieldErrors.username ? "has-error" : ""}`}>
            <label htmlFor="auth-username">Имя пользователя</label>
            <input
              id="auth-username"
              type="text"
              autoComplete="username"
              value={username}
              onChange={(e) => {
                setUsername(e.target.value);
                clearFieldError("username");
              }}
              autoFocus
            />
            {fieldErrors.username ? (
              <span className="field-error">{fieldErrors.username}</span>
            ) : null}
          </div>
        ) : null}

        <div className={`field ${fieldErrors.email ? "has-error" : ""}`}>
          <label htmlFor="auth-email">Email</label>
          <input
            id="auth-email"
            type="email"
            autoComplete="email"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
              clearFieldError("email");
            }}
            autoFocus={!isRegister}
          />
          {fieldErrors.email ? <span className="field-error">{fieldErrors.email}</span> : null}
        </div>

        <div className={`field ${fieldErrors.password ? "has-error" : ""}`}>
          <label htmlFor="auth-password">Пароль</label>
          <input
            id="auth-password"
            type="password"
            autoComplete={isRegister ? "new-password" : "current-password"}
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
              clearFieldError("password");
            }}
          />
          {fieldErrors.password ? (
            <span className="field-error">{fieldErrors.password}</span>
          ) : null}
        </div>

        {isRegister ? (
          <div className={`field ${fieldErrors.repeatPassword ? "has-error" : ""}`}>
            <label htmlFor="auth-repeat-password">Повторите пароль</label>
            <input
              id="auth-repeat-password"
              type="password"
              autoComplete="new-password"
              value={repeatPassword}
              onChange={(e) => {
                setRepeatPassword(e.target.value);
                clearFieldError("repeatPassword");
              }}
            />
            {fieldErrors.repeatPassword ? (
              <span className="field-error">{fieldErrors.repeatPassword}</span>
            ) : null}
          </div>
        ) : null}

        <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
          {submitting ? "Подождите…" : copy.submitLabel}
        </button>

        <p className="auth-switch">
          {copy.switchHint}{" "}
          <button type="button" className="auth-switch__link" onClick={onSwitchMode}>
            {copy.switchLabel}
          </button>
        </p>
      </form>
    </Modal>
  );
}

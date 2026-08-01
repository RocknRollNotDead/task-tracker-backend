import { createContext, useCallback, useContext, useEffect, useState } from "react";
import {
  fetchCurrentUser,
  login as apiLogin,
  register as apiRegister,
  logout as apiLogout,
} from "../api/authApi";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [initializing, setInitializing] = useState(true);

  // При загрузке приложения проверяем, есть ли активная сессия (cookie).
  // 401 здесь — ожидаемый, обычный случай "пользователь не залогинен",
  // а не ошибка, которую нужно показывать.
  useEffect(() => {
    let cancelled = false;
    fetchCurrentUser()
      .then((currentUser) => {
        if (!cancelled) setUser(currentUser);
      })
      .catch(() => {
        if (!cancelled) setUser(null);
      })
      .finally(() => {
        if (!cancelled) setInitializing(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  const login = useCallback(async (credentials) => {
    const loggedInUser = await apiLogin(credentials);
    setUser(loggedInUser);
    return loggedInUser;
  }, []);

  const register = useCallback(async (credentials) => {
    const createdUser = await apiRegister(credentials);
    setUser(createdUser);
    return createdUser;
  }, []);

  const logout = useCallback(async () => {
    try {
      await apiLogout();
    } finally {
      setUser(null);
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, initializing, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth должен использоваться внутри <AuthProvider>");
  return ctx;
}

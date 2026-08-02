import { useAuth } from "../../context/AuthContext.jsx";
import "./Header.css";

export default function Header({ onOpenLogin, onOpenRegister }) {
  const { user, logout } = useAuth();

  return (
    <header className="app-header">
      <div className="app-header__brand">
        <span className="app-header__mark">▣</span>
        <span className="app-header__wordmark">TASK / LEDGER</span>
      </div>

      {user ? (
        <div className="app-header__account">
          <span className="app-header__avatar" aria-hidden="true">
            {(user.username || user.email)?.[0]?.toUpperCase() || "?"}
          </span>
          {}
          <span className="app-header__email">{user.username || user.email}</span>
          <button type="button" className="btn btn-ghost btn-sm" onClick={logout}>
            Выйти
          </button>
        </div>
      ) : (
        <div className="app-header__actions">
          <button type="button" className="btn btn-ghost btn-sm" onClick={onOpenLogin}>
            Войти
          </button>
          <button type="button" className="btn btn-primary btn-sm" onClick={onOpenRegister}>
            Регистрация
          </button>
        </div>
      )}
    </header>
  );
}

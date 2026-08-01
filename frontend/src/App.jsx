import { useState } from "react";
import Header from "./components/Header/Header.jsx";
import AuthModal from "./components/Auth/AuthModal.jsx";
import TaskComposer from "./components/Tasks/TaskComposer.jsx";
import TaskList from "./components/Tasks/TaskList.jsx";
import TaskDetailModal from "./components/Tasks/TaskDetailModal.jsx";
import NewTaskModal from "./components/Tasks/NewTaskModal.jsx";
import { useAuth } from "./context/AuthContext.jsx";
import { useTasks } from "./hooks/useTasks.js";
import "./components/Tasks/Tasks.css";

export default function App() {
  const { user, initializing } = useAuth();
  const [authMode, setAuthMode] = useState(null); // "login" | "register" | null
  const [selectedTaskId, setSelectedTaskId] = useState(null);
  const [draftTaskName, setDraftTaskName] = useState(null); // строка -> открыть модалку создания

  const { tasks, loading, error, addTask, toggleTask, persistEdit, removeTask } = useTasks(
    Boolean(user)
  );

  const selectedTask = tasks.find((t) => t.id === selectedTaskId) || null;

  if (initializing) {
    return <div className="state-banner">Загрузка…</div>;
  }

  return (
    <>
      <Header onOpenLogin={() => setAuthMode("login")} onOpenRegister={() => setAuthMode("register")} />

      {user ? (
        <main className="content">
          <div className="content__intro">
            <h1>Журнал задач</h1>
            <p>Вписывайте новые дела наверху, отмечайте выполненные внутри карточки.</p>
          </div>

          <TaskComposer onRequestCreate={setDraftTaskName} />

          {loading ? (
            <div className="state-banner">Загружаю записи…</div>
          ) : error ? (
            <div className="state-banner state-banner--error">{error}</div>
          ) : (
            <TaskList tasks={tasks} onOpen={setSelectedTaskId} onToggle={toggleTask}  />
          )}
        </main>
      ) : (
        <div className="landing">
          <div className="landing__mark">▣</div>
          <h1>Task Ledger</h1>
          <p>Личный журнал задач. Войдите или зарегистрируйтесь, чтобы начать вести записи.</p>
          <div className="landing__actions">
            <button type="button" className="btn btn-ghost" onClick={() => setAuthMode("login")}>
              Войти
            </button>
            <button type="button" className="btn btn-primary" onClick={() => setAuthMode("register")}>
              Регистрация
            </button>
          </div>
        </div>
      )}

      {authMode ? (
        <AuthModal
          mode={authMode}
          onClose={() => setAuthMode(null)}
          onSwitchMode={() => setAuthMode(authMode === "login" ? "register" : "login")}
        />
      ) : null}

      {draftTaskName !== null ? (
        <NewTaskModal
          initialName={draftTaskName}
          onClose={() => setDraftTaskName(null)}
          onCreate={({ name, text }) => addTask(name, text)}
        />
      ) : null}

      {selectedTask ? (
        <TaskDetailModal
          task={selectedTask}
          onClose={() => setSelectedTaskId(null)}
          onToggle={toggleTask}
          onSave={persistEdit}
          onDelete={removeTask}
        />
      ) : null}
    </>
  );
}

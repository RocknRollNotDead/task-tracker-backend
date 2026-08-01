import { useEffect, useRef, useState } from "react";
import Modal from "../common/Modal.jsx";
import { useDebouncedCallback } from "../../hooks/useDebouncedCallback.js";
import { isDone } from "../../hooks/useTasks.js";
import { ApiError } from "../../api/httpClient.js";

const DEBOUNCE_MS = (window.APP_CONFIG || {}).AUTOSAVE_DEBOUNCE_MS ?? 600;

const SAVE_STATE_LABEL = {
  idle: "",
  pending: "Есть несохранённые изменения…",
  saving: "Сохраняю…",
  saved: "Сохранено",
  error: "Не удалось сохранить",
};

export default function TaskDetailModal({ task, onClose, onToggle, onSave, onDelete }) {
  const [name, setName] = useState(task.name || "");
  const [text, setText] = useState(task.text || "");
  const [saveState, setSaveState] = useState("idle");
  const [toggling, setToggling] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState(null);

  // Если снаружи прилетело новое состояние задачи (например, после toggle),
  // синхронизируем поля, но только когда они реально изменились сервером,
  // а не затираем то, что человек сейчас печатает.
  const lastSyncedRef = useRef({ name: task.name, text: task.text });
  useEffect(() => {
    if (task.name !== lastSyncedRef.current.name || task.text !== lastSyncedRef.current.text) {
      setName(task.name || "");
      setText(task.text || "");
      lastSyncedRef.current = { name: task.name, text: task.text };
    }
  }, [task.name, task.text]);

  const debouncedSave = useDebouncedCallback(async (nextName, nextText) => {
    setSaveState("saving");
    try {
      const updated = await onSave(task.id, { name: nextName, text: nextText });
      lastSyncedRef.current = { name: updated?.name ?? nextName, text: updated?.text ?? nextText };
      setSaveState("saved");
      setTimeout(() => setSaveState((s) => (s === "saved" ? "idle" : s)), 1500);
    } catch (err) {
      setSaveState("error");
    }
  }, DEBOUNCE_MS);

  function handleNameChange(e) {
    const next = e.target.value;
    setName(next);
    setSaveState("pending");
    debouncedSave(next, text);
  }

  function handleTextChange(e) {
    const next = e.target.value;
    setText(next);
    setSaveState("pending");
    debouncedSave(name, next);
  }

  async function handleToggle() {
    setToggling(true);
    try {
      await onToggle(task.id);
    } finally {
      setToggling(false);
    }
  }

  async function handleDelete() {
    setDeleting(true);
    setDeleteError(null);
    try {
      await onDelete(task.id);
      onClose();
    } catch (err) {
      setDeleteError(err instanceof ApiError ? err.message : "Не удалось удалить задачу");
      setDeleting(false);
    }
  }

  const done = isDone(task);

  return (
    <Modal
      title="Задача"
      eyebrow={`#${String(task.id).padStart(4, "0")}`}
      onClose={onClose}
      width="md"
      footer={
        <>
          <button type="button" className="btn btn-danger btn-sm" onClick={handleDelete} disabled={deleting}>
            {deleting ? "Удаляю…" : "Удалить"}
          </button>
          <span className={`save-indicator save-indicator--${saveState}`}>
            {SAVE_STATE_LABEL[saveState]}
          </span>
        </>
      }
    >
      {deleteError ? <div className="form-banner">{deleteError}</div> : null}

      <div className="field">
        <label htmlFor="task-name">Заголовок</label>
        <input id="task-name" type="text" value={name} onChange={handleNameChange} />
      </div>

      <div className="field">
        <label htmlFor="task-text">Описание</label>
        <textarea id="task-text" rows={6} value={text} onChange={handleTextChange} />
      </div>

      <label className="task-done-toggle">
        <input type="checkbox" checked={done} disabled={toggling} onChange={handleToggle} />
        <span>Задача сделана</span>
      </label>
    </Modal>
  );
}

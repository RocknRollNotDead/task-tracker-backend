import { useState } from "react";

/**
 * Composer больше не создаёт задачу напрямую: он только собирает заголовок
 * и просит родителя открыть модалку создания (NewTaskModal), где уже есть
 * поле текста и кнопка отправки.
 */
export default function TaskComposer({ onRequestCreate }) {
  const [value, setValue] = useState("");
  const [error, setError] = useState(null);

  function handleSubmit(e) {
    e.preventDefault();
    const name = value.trim();
    if (!name) {
      setError("Введите заголовок задачи");
      return;
    }
    setError(null);
    onRequestCreate(name);
    setValue("");
  }

  return (
    <form className="composer" onSubmit={handleSubmit}>
      <div className="composer__row">
        <input
          className="composer__input"
          type="text"
          placeholder="Новая запись в журнал…"
          value={value}
          onChange={(e) => {
            setValue(e.target.value);
            if (error) setError(null);
          }}
          aria-label="Заголовок новой задачи"
        />
        <button type="submit" className="btn btn-primary">
          Добавить
        </button>
      </div>
      {error ? <div className="composer__error">{error}</div> : null}
    </form>
  );
}

import { isDone } from "../../hooks/useTasks.js";

export default function TaskRow({ task, index, onOpen, onToggle }) {
    const done = isDone(task);

    function handleStampContextMenu(e) {
        e.preventDefault();   // не даём открыться нативному контекстному меню браузера
        e.stopPropagation();  // не даём событию всплыть до кнопки-строки
        if (!done) onToggle(task.id);
    }

    return (
        <li>
            <button type="button" className={`task-row ${done ? "task-row--done" : ""}`} onClick={() => onOpen(task.id)}>
                <span className="task-row__index">{String(index + 1).padStart(3, "0")}</span>
                <span
                    className={`task-row__stamp ${done ? "task-row__stamp--done" : ""}`}
                    onContextMenu={handleStampContextMenu}
                    title={done ? "Выполнено" : "ПКМ — отметить выполненной"}
                >
          {done ? "✓" : ""}
        </span>
                <span className="task-row__name">{task.name}</span>
                {task.text ? <span className="task-row__hint">описание есть</span> : null}
            </button>
        </li>
    );
}
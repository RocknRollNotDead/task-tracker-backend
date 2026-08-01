import { isDone } from "../../hooks/useTasks.js";
import TaskRow from "./TaskRow.jsx";

function TaskSection({ title, tasks, emptyHint, onOpen, onToggle }) {
    return (
        <section className="task-section">
            <div className="task-section__header">
                <h3>{title}</h3>
                <span className="task-section__count">{tasks.length}</span>
            </div>
            {tasks.length === 0 ? (
                <p className="task-section__empty">{emptyHint}</p>
            ) : (
                <ul className="task-list">
                    {tasks.map((task, i) => (
                        <TaskRow key={task.id} task={task} index={i} onOpen={onOpen} onToggle={onToggle} />
                    ))}
                </ul>
            )}
        </section>
    );
}

export default function TaskList({ tasks, onOpen, onToggle }) {
    const open = tasks.filter((t) => !isDone(t));
    const done = tasks.filter((t) => isDone(t));

    return (
        <div className="task-columns">
            <TaskSection
                title="Открытые"
                tasks={open}
                emptyHint="Пусто. Впишите первую строку выше."
                onOpen={onOpen}
                onToggle={onToggle}
            />
            <TaskSection
                title="Сделано"
                tasks={done}
                emptyHint="Ни одной закрытой записи пока нет."
                onOpen={onOpen}
                onToggle={onToggle}
            />
        </div>
    );
}
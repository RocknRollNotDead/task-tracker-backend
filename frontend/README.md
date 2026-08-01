# Task Ledger — фронтенд

React (Vite) SPA для Task Tracker. Всё общение с сервером — через `fetch` (Ajax),
интерфейс обновляется динамически без перезагрузки страницы.

## Запуск

```bash
npm install
npm run dev       # http://localhost:5173, проксирует /api на localhost:8080
```

Сборка для продакшна:

```bash
npm run build      # результат в dist/
npm run preview
```

## Конфигурация — `public/config.js`

Это единственное место, которое нужно менять при деплое на другой адрес API —
пересборка не требуется, файл не проходит через Vite-бандлер:

```js
window.APP_CONFIG = {
  API_BASE_URL: "",        // напр. "https://api.example.com"
  API_PREFIX: "/api",
  ENDPOINTS: { LOGIN: "/auth/login", ... },
  WITH_CREDENTIALS: true,  // сессия на cookie (Spring Security)
  TASK_STATUS: { DONE: "DONE", NOT_DONE: "NOT_DONE" },
  AUTOSAVE_DEBOUNCE_MS: 600,
};
```

## Структура

```
src/
  api/            httpClient.js (fetch-обёртка + разбор ошибок), authApi.js, tasksApi.js
  context/        AuthContext — текущий пользователь, login/register/logout
  hooks/          useTasks (CRUD задач), useDebouncedCallback (автосохранение)
  components/
    Header/       шапка с кнопками входа/регистрации либо email + Logout
    Auth/         AuthModal — общая модалка для входа и регистрации
    Tasks/        TaskComposer, TaskList, TaskRow, TaskDetailModal
    common/       Modal — переиспользуемое модальное окно
```

Каждый API-вызов вынесен в `src/api/*`, поэтому добавление нового эндпоинта или
смена контракта не требует правок в компонентах — меняется только слой API.

## Важные несоответствия с текущим бэкендом

1. ~~`RequestAuthDto.username`~~ — **решено.** Форма регистрации теперь
   содержит настоящее поле `username`, отправляется как есть
   (`src/api/authApi.js`, `register()`). Форма входа по-прежнему без
   username в UI — туда прозрачно подставляется `username = email`, как и
   раньше.

   **Осталось на бэкенде:** `UserDto` содержит только `{ id, email }`.
   Чтобы шапка приложения могла показывать `username` (а не откатываться на
   email), нужно добавить поле `username` в `UserDto` и прокинуть его из
   `UserService.createUser` / `getInfo`. До этого момента фронтенд сам
   определяет: `user.username || user.email` (см. `Header.jsx`).

2. ~~`PATCH /tasks/edit` через `@RequestParam RequestTaskDto dto`~~ —
   **решено** после того, как на бэке заменили на `@RequestBody`.
   `editTask()` в `src/api/tasksApi.js` теперь шлёт `taskId` query-параметром,
   а `{ name, text }` — JSON-телом.

3. **Базовый путь контроллеров** — `AuthController`, `UserController`,
   `TaskController` замаплены на `/auth`, `/user`, `/tasks` (без `/api`), а
   `SecurityConfig` пускает без авторизации `/api/auth/**` и логаутит на
   `/api/auth/sign-out`. Предположил, что у приложения есть общий
   context-path `/api` (`server.servlet.context-path=/api` в
   `application.properties`), поэтому в `public/config.js` стоит
   `API_PREFIX: "/api"`. Если это не так — поставьте там `""`.
4. **CORS** — если фронтенд и бэкенд будут на разных портах/доменах в проде,
   на бэкенде нужен `CorsConfigurationSource` с `allowCredentials(true)` и
   явным списком origin (браузер не отправит cookie сессии без этого).

## Поведение UI

- Ошибки регистрации/входа показываются под конкретными полями формы, если
  бэкенд возвращает `{ "field": "message" }` (или список
  `[{ field, defaultMessage }]`, как в стандартном ответе Spring на
  `MethodArgumentNotValidException`); иначе — баннером сверху формы.
- Задача открывается в модалке по клику на строку. Поля заголовка/описания
  сохраняются автоматически через 600 мс после последнего изменения — кнопки
  "Сохранить" нет. Чекбокс "сделано" отправляет запрос сразу.
- Список задач разбит на "Открытые" и "Сделано" на основе поля `status`
  (сопоставление значений — в `public/config.js`, `TASK_STATUS`).
- Создание задачи — двухшаговое: поле в верхней панели собирает только
  заголовок, кнопка "Добавить" открывает `NewTaskModal`
  (`src/components/Tasks/NewTaskModal.jsx`) с заголовком (можно
  скорректировать) и описанием, и там уже есть кнопка "Отправить", которая
  реально создаёт задачу через API. В отличие от модалки редактирования, тут
  сохранение явное, а не автосейв — так и задумано, задачи ещё не существует
  на сервере, автосохранять нечего.

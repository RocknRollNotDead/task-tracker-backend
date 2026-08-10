# Планировщик задач

Шестой (седьмой по старому счёту) учебный проект из [роадмапа Сергея Жукова](https://zhukovsd.github.io/java-backend-learning-course/).
[ТЗ проекта](https://zhukovsd.github.io/java-backend-learning-course/projects/task-tracker/).

Деплой на https://task-tracker.codeportfolio.ru/

## Стек и структура

#### Проект состоит из нескольких сервисов

**Backend**

- Основной сервис, запросы от фронтенда идут только к нему
- REST API на Spring Boot со Spring Security, а в качестве хранения данных используются postgresql

**Frontend**

- [навайбкодил](https://github.com/RocknRollNotDead/task-tracker-frontend)

**Email Sender**

- Помощник бэкенду, RPC API на Spring Boot со Spring Mail, находится
  в [другом репозитории](https://github.com/RocknRollNotDead/task-tracker-email-sender)

**Планировщик**

- Помощник бэкенду, RPC API на Spring Boot со Spring Scheduler, находится
  в [другом репозитории](https://github.com/RocknRollNotDead/task-tracker-scheduler)

**Сервис суммаризации**

- Помощник бэкенду, RPC API на Spring Boot с интеграцией AI API с Nemotron от Nvidia, находится
  в [другом репозитории](https://github.com/RocknRollNotDead/task-tracker-summarizer)

Все сервисы кроме фронтенда общаются друг с другом по RPC через Kafka, и имеют отдельные репозитории. 
Все репозитории при пуше собирают образ на https://hub.docker.com/repositories/appleapplenotdead
Деплой у меня происходит по кнопке в Actions в репозитории бэкенда.

## Функциональность

Все эндпоинты бэкенда находятся под общим путём `/api`. Пример: `/api/auth/sign-up`.

---

#### Регистрация и авторизация

| Метод | Путь           | Описание          |
|-------|----------------|-------------------|
| POST  | `/user`        | Регистрация       |
| POST  | `/auth/login`  | Авторизация       |
| POST  | `/auth/logout` | Выход из аккаунта |

---

#### Задачи

| Метод  | Путь          | Описание                    |
|--------|---------------|-----------------------------|
| POST   | `/tasks`      | Создать                     |
| PATCH  | `/tasks`      | Пометить задачу выполненной |
| PATCH  | `/tasks/edit` | Отредактировать задачу      |
| DELETE | `/tasks`      | Удалить задачу              |

---------

#### Пользователи

**GET `/user`** — текущий авторизованный пользователь

#### Общие коды ответа

Используются во всех эндпоинтах API.

| Код                         | Значение                                                                          |
|-----------------------------|-----------------------------------------------------------------------------------|
| `200 OK`                    | Успешный запрос, тело ответа содержит запрошенные данные                          |
| `201 Created`               | Успешное создание задачи                                                          |
| `204 No Content`            | Успешный запрос, тело ответа отсутствует (логаут, удаление)                       |
| `400 Bad Request`           | Ошибка валидации — невалидный или отсутствующий параметр, невалидное тело запроса |
| `401 Unauthorized`          | Запрос выполняется неавторизованным пользователем или невалидным jwt              |
| `404 Not Found`             | Запрошенные данные не найдены                                                     |
| `409 Conflict`              | Конфликт — данные с таким идентификатором уже существуют (email)                  |
| `500 Internal Server Error` | Непредвиденная ошибка на сервере                                                  |

---

## Как деплоить

### 1. Зайти в Ubuntu

- Арендовать vps сервер с Ubuntu на одном из провайдеров. Российские, такие, как [Beget](https://beget.com), не
  рекомендую.
- Там будут данные для входа в виде ssh login@000.000.000.000 и ssh ключ, где вместо login - выданный логин, вместо
  0.0.0.0 выданный ip адрес, а ssh ключ в формате файла вместо пароля, либо в формате текста на несколько строчек.
- Открыть командную строку БЕЗ имени администратора и ввести 'ssh login@000.000.000.000' * Enter * и потом password: '
  mypassword' для захода в линукс терминал на сервере, либо сохранить файл с ssh ключом в папку C:\Users\user\.ssh\

### 2. Настроить Docker

**2.1 установить Docker и docker-compose**

```bash
apt update && apt upgrade -y
sudo apt install -y docker.io docker-compose-v2
```

или

```bash
sudo snap install docker
```


**2.2 собрать Dockerfile и docker-compose.yml**

```yaml
services:
  db:
    ...
  app:
    image: usernameondockerhub/task-tracker-backend:latest
    ...
  frontend:
    image: usernameondockerhub/task-tracker-frontend:latest
    ...
  service-2:
    image:
    ...
volumes:
```

собрать образы, предварительно авторизовавшись на гитхаб/докерхаб с помощью токена

```bash
docker login -u usernameondockerhub
```


```bash
docker build -t usernameondockerhub/task-tracker-backend:latest .
docker push -t usernameondockerhub/task-tracker-backend:latest .
```

**2.3 отправить docker-compose на сервер**

```bash
scp -r docker-compose.yml root@000.000.0.000:~/tasker
```

отправляет в директорию `пользователь/tasker` на удалённом сервере

**2.4 запустить docker-compose**

```bash
docker compose up -d
```

**2.5 исправление багов**

посмотреть логи

```bash
docker logs container_name
```

исправить на своём компьютере и в cmd не заходя на удалённый сервер отправить

```bash
scp -r docker-compose.yml root@000.000.0.000:~/tasker/
```

и потом на удалённом сервере

```bash
docker compose pull
docker compose up -d
```

и потом посмотреть логи

```bash
docker exec -it app_container_name bash
ls -la /usr/local/tomcat/logs
```

### 3. Перенос на домен

Добавить настройку nginx в docker-compose

```yaml
  nginx-proxy:

  acme:

```

acme сам получит SSL сертификат по переменным

```yaml
    frontend:
      VIRTUAL_HOST:
      VIRTUAL_PORT:
      LETSENCRYPT_HOST:
      LETSENCRYPT_EMAIL:
```

В регистраторе хостинга потом ещё надо добавить А-запись с субдоменом `task-tracker` или другой, какой я захочу, когда
буду деплоить,
с перенаправлением на тот ip адрес, где будет развёрнут фронтенд.

И после этого всё приложение доступно по https://task-tracker.codeportfolio.ru (https с SSL сертификатом)

## О том, что изучил на этом проекте

Spring Boot, JWT, Spring Scheduler, Spring Mail, Kafka и GitHub Actions (CI/CD)
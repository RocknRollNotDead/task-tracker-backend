
# Планировщик задач

Седьмой учебный проект из [роадмапа Сергея Жукова](https://zhukovsd.github.io/java-backend-learning-course/).
[ТЗ проекта](https://zhukovsd.github.io/java-backend-learning-course/projects/task-tracker/).

Планирую деплоить на https://task-tracker.codeportfolio.ru/ (ещё не думал об этом)

## Стек и структура

#### Проект состомт из нескольких сервисов

**Backend**

- Основной сервис, запросы от фронтенда идут только к нему
- REST API на Spring Boot со Spring Security, а в качестве хранения используются postgresql

**Frontend** 

- [навайбкодил](https://github.com/RocknRollNotDead/task-tracker-frontend)

**Email Sender**

- Помощник бэкенду, RPC API на Spring Boot со Spring Mail, находится в [другом репозитории](https://github.com/RocknRollNotDead/task-tracker-email-sender)

**Планировщик**

- Помощник бэкенду, RPC API на Spring Boot со Spring Scheduler

**Сервис суммаризации**

- Помощник бэкенду, RPC API на Spring Boot с интеграцией AI API, скорее всего это будет либо Claude, либо бесплатный Grok

Все сервисы кроме фронтенда общаются друг с другом по RPC через Kafka, и имеют отдельные репозитории.


## Функциональность

Все эндпоинты находятся под общим путём `/api`. Пример: `/api/auth/sign-up`.

---

#### Регистрация и авторизация

| Метод | Путь                | Описание |
|---|---------------------|---|
| POST | `/user`             | Регистрация |
| POST | `/auth/login`       | Авторизация |
| POST | `/auth/sign-out`    | Выход из аккаунта |

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

| Код | Значение |
|---|---|
| `200 OK` | Успешный запрос, тело ответа содержит запрошенные данные |
| `201 Created` | Успешное создание ресурса (регистрация, загрузка файла, создание папки) |
| `204 No Content` | Успешный запрос, тело ответа отсутствует (логаут, удаление) |
| `400 Bad Request` | Ошибка валидации — невалидный или отсутствующий параметр, невалидное тело запроса |
| `401 Unauthorized` | Запрос выполняется неавторизованным пользователем |
| `404 Not Found` | Запрошенный ресурс не найден |
| `409 Conflict` | Конфликт — ресурс с таким именем/путём уже существует |
| `500 Internal Server Error` | Непредвиденная ошибка на сервере |

---



## Как деплоить

### 1. Зайти в Ubuntu

- Арендовать vps сервер с Ubuntu на одном из  провайдеров. Российские, такие, как [Beget](https://beget.com), не рекомендую.
- Там будут данные для входа в виде ssh login@000.000.000.000 и password, где вместо login - выданный логин, вместо 0.0.0.0 выданный ip адрес, а вместо password - выданный пароль
- Открыть командную строку БЕЗ имени администратора и ввести 'ssh login@000.000.000.000' * Enter * и потом password: 'mypassword' для захода в линукс терминал на сервере

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
    image: ghcr.io/usernameonhub/task-tracker-backend:latest
    ...
  frontend:
    image: ghcr.io/usernameonhub/task-tracker-frontend:latest
    ...
  service-2:
    image:
    ...
volumes:
```

собрать образы, предварительно авторизовавшись на гитхаб/докерхаб с помощью токена

```bash
docker build -t ghcr.io/usernameonhub/task-tracker-backend:latest .
docker push -t ghcr.io/usernameonhub/task-tracker-backend:latest .
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
scp -r docker-compose.yml root@000.000.0.000:~/task-tracker-backend/
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

В регистраторе хостинга потом ещё надо добавить А-запись с субдоменом `task-tracker` или другой, какой я захочу, когда буду деплоить, 
с перенаправлением на тот ip адрес, где будет развёрнут фронтенд.

И после этого всё приложение будет доступно по https://task-tracker.codeportfolio.ru (https с SSL сертификатом)


## О том, что изучу на этом проекте

Spring Boot, JWT, Spring Scheduler, Spring Mail, Kafka и GitHub Actions (CI/CD)
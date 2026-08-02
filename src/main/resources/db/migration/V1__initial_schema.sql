CREATE TABLE users (
                       id       BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email    VARCHAR(255) NOT NULL,
                       role     INTEGER      NOT NULL,

                       CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE tasks (
                       id        BIGSERIAL PRIMARY KEY,
                       name      VARCHAR(255) NOT NULL,
                       text      TEXT         NOT NULL,
                       owner_id  BIGINT       NOT NULL,
                       status    INTEGER      NOT NULL,
                       timestamp TIMESTAMP    NULL,

                       CONSTRAINT fk_tasks_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE INDEX idx_tasks_owner_id ON tasks (owner_id);
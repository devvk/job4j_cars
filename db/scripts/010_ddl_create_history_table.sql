--liquibase formatted sql

--changeset devvk:009_ddl_create_history_table
CREATE TABLE history
(
    id       SERIAL PRIMARY KEY,
    start_at TIMESTAMP NOT NULL,
    end_at   TIMESTAMP NOT NULL
);

--rollback DROP TABLE history;

--liquibase formatted sql

--changeset devvk:010_ddl_create_history_table
CREATE TABLE history
(
    id       SERIAL PRIMARY KEY,
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    end_at   TIMESTAMP WITH TIME ZONE NOT NULL
);

--rollback DROP TABLE history;

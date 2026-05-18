--liquibase formatted sql

--changeset devvk:001_ddl_create_users_table
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    login    TEXT NOT NULL,
    password TEXT NOT NULL,
    UNIQUE (login)
);

--rollback DROP TABLE users;

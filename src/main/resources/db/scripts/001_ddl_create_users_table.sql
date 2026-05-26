--liquibase formatted sql

--changeset devvk:001_ddl_create_users_table
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    login    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

--rollback DROP TABLE users;

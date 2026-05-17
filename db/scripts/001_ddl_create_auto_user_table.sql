--liquibase formatted sql

--changeset devvk:001_ddl_create_auto_user_table
CREATE TABLE auto_user
(
    id       SERIAL PRIMARY KEY,
    login    VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    UNIQUE (login)
);

--rollback DROP TABLE auto_user;

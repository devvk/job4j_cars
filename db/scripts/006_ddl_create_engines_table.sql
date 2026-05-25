--liquibase formatted sql

--changeset devvk:006_ddl_create_engines_table
CREATE TABLE engines
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

--rollback DROP TABLE engines;

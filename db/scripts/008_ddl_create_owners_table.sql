--liquibase formatted sql

--changeset devvk:008_ddl_create_owners_table
CREATE TABLE owners
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    user_id INT          NOT NULL REFERENCES users (id)
);

--rollback DROP TABLE owners;

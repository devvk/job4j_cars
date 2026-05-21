--liquibase formatted sql

--changeset devvk:005_ddl_create_participates_table
CREATE TABLE participates
(
    id      SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    post_id INT NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    UNIQUE (user_id, post_id)
);

--rollback DROP TABLE participates;

--liquibase formatted sql

--changeset devvk:002_ddl_create_posts_table
CREATE TABLE posts
(
    id      SERIAL PRIMARY KEY,
    description TEXT                      NOT NULL,
    created TIMESTAMP NOT NULL,
    user_id     INT REFERENCES users (id) NOT NULL
);

--rollback DROP TABLE posts;

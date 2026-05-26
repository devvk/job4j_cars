--liquibase formatted sql

--changeset devvk:002_ddl_create_posts_table
CREATE TABLE posts
(
    id          SERIAL PRIMARY KEY,
    description TEXT                     NOT NULL,
    created     TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id     INT                      NOT NULL REFERENCES users (id)
);

--rollback DROP TABLE posts;

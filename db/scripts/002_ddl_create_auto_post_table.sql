--liquibase formatted sql

--changeset devvk:002_ddl_create_auto_post_table
CREATE TABLE auto_post
(
    id           SERIAL PRIMARY KEY,
    description  VARCHAR                       NOT NULL,
    created      TIMESTAMP                     NOT NULL,
    auto_user_id INT REFERENCES auto_user (id) NOT NULL
);

--rollback DROP TABLE auto_post;

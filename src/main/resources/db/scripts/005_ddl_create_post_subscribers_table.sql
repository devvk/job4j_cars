--liquibase formatted sql

--changeset devvk:005_ddl_create_post_subscribers_table
CREATE TABLE post_subscribers
(
    user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    post_id INT NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, post_id)
);

--rollback DROP TABLE post_subscribers;

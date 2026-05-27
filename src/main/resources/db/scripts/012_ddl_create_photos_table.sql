--liquibase formatted sql

--changeset devvk:012_ddl_create_photos_table
CREATE TABLE photos
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    path    VARCHAR(255) NOT NULL,
    post_id INT          NOT NULL REFERENCES posts (id)
);

--rollback DROP TABLE photos;

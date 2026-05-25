--liquibase formatted sql

--changeset devvk:012_ddl_add_photo_to_posts_table
ALTER TABLE posts
    ADD COLUMN photo VARCHAR;

--rollback ALTER TABLE posts DROP COLUMN photo;

--liquibase formatted SQL

--changeset devvk:013_ddl_alter_posts_add_sold
ALTER TABLE posts
    ADD COLUMN sold BOOLEAN NOT NULL DEFAULT FALSE;

--rollback ALTER TABLE posts DROP COLUMN sold;

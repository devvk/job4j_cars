--liquibase formatted SQL

--changeset devvk:011_ddl_alter_posts_add_car_id
ALTER TABLE posts
    ADD COLUMN car_id INT NOT NULL REFERENCES cars (id);

--rollback ALTER TABLE posts DROP COLUMN car_id;

--liquibase formatted sql

--changeset devvk:011_ddl_add_car_id_to_cars_table
ALTER TABLE posts
    ADD COLUMN car_id INT REFERENCES cars (id);

--rollback ALTER TABLE post DROP COLUMN car_id;

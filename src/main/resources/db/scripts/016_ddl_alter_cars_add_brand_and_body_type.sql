--liquibase formatted sql

--changeset devvk:016_ddl_alter_cars_add_brand_and_body_type
ALTER TABLE cars
    RENAME COLUMN name TO model;

ALTER TABLE cars
    ADD COLUMN brand_id INT REFERENCES brands (id);

ALTER TABLE cars
    ADD COLUMN body_type VARCHAR(255);

--rollback ALTER TABLE cars DROP COLUMN body_type;
--rollback ALTER TABLE cars DROP COLUMN brand_id;
--rollback ALTER TABLE cars RENAME COLUMN model TO name;
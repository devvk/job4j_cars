--liquibase formatted sql

--changeset devvk:014_ddl_add_brand_id_to_cars_table
ALTER TABLE cars
    ADD COLUMN brand_id INT REFERENCES brands (id);

--rollback ALTER TABLE cars DROP COLUMN brand_id;

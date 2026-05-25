--liquibase formatted sql

--changeset devvk:013_ddl_create_brands_table
CREATE TABLE brands
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

--rollback DROP TABLE brands;

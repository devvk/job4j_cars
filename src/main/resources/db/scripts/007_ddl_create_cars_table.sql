--liquibase formatted sql

--changeset devvk:007_ddl_create_cars_table
CREATE TABLE cars
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    engine_id INT          NOT NULL REFERENCES engines (id)
);

--rollback DROP TABLE cars;

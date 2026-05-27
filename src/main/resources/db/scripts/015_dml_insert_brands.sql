--liquibase formatted sql

--changeset devvk:015_dml_insert_brands
INSERT INTO brands (name)
VALUES ('Toyota'),
       ('BMW'),
       ('Mercedes-Benz'),
       ('Audi'),
       ('Volkswagen'),
       ('Ford'),
       ('Hyundai'),
       ('Kia'),
       ('Nissan'),
       ('Renault'),
       ('Lada'),
       ('Skoda');

--rollback DELETE FROM brands;
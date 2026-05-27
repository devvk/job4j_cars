--liquibase formatted sql

--changeset devvk:017_dml_insert_engines
INSERT INTO engines (name)
VALUES ('Бензиновый'),
       ('Дизельный'),
       ('Гибридный'),
       ('Электрический'),
       ('Газовый');

--rollback DELETE FROM engines;
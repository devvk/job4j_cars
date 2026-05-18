--liquibase formatted sql

--changeset devvk:003_dml_insert
INSERT INTO users (login, password)
VALUES ('ivanov', 'password');
INSERT INTO users (login, password)
VALUES ('petrov', 'password');
INSERT INTO users (login, password)
VALUES ('sidorov', 'password');

--rollback DELETE FROM users
--rollback WHERE login IN ('ivanov', 'petrov', 'sidorov');

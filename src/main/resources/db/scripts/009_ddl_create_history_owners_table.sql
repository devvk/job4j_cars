--liquibase formatted sql

--changeset devvk:009_ddl_create_history_owners_table
CREATE TABLE history_owners
(
    car_id   INT NOT NULL REFERENCES cars (id) ON DELETE CASCADE,
    owner_id INT NOT NULL REFERENCES owners (id) ON DELETE CASCADE,
    PRIMARY KEY (car_id, owner_id)
);

--rollback DROP TABLE history_owners;

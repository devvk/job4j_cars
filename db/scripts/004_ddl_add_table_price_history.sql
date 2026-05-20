--liquibase formatted sql

--changeset devvk:004_ddl_add_table_price_history
CREATE TABLE PRICE_HISTORY
(
    id      SERIAL PRIMARY KEY,
    before  BIGINT NOT NULL,
    after   BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    post_id INT REFERENCES posts (id)
);

--rollback DROP TABLE price_history;
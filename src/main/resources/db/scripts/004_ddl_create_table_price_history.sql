--liquibase formatted SQL

--changeset devvk:004_ddl_create_table_price_history
CREATE TABLE price_history
(
    id      SERIAL PRIMARY KEY,
    before  BIGINT                   NOT NULL,
    after   BIGINT,
    created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    post_id INT                      NOT NULL REFERENCES posts (id)
);

--rollback DROP TABLE price_history;
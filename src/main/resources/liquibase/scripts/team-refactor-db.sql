-- liquibase formatted sql

-- changeset mk:1

CREATE TABLE recommendation
(
    name        text NOT NULL,
    id          uuid PRIMARY KEY,
    rules       TEXT[],
    description TEXT,
    users       uuid[]
);

-- changeset mk:2

ALTER TABLE recommendation
    ADD COLUMN rules_query JSONB[]
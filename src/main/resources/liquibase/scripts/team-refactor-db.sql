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
    ADD COLUMN rules_query JSONB[];

-- changeset mk:3

alter table recommendation
add column user_trigger_incremental_load int8;

-- changeset mk:4

ALTER TABLE recommendation ALTER COLUMN user_trigger_incremental_load SET DEFAULT 1;
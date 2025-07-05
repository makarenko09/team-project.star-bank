-- liquibase formatted sql

-- changeset durov:1

CREATE INDEX students_name_index ON student (name);
create index faculties_name_color_index on faculty (name,color);
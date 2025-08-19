-- liquibase formatted sql

-- changeset za:create_rule_stats_table

CREATE TABLE IF NOT EXISTS rule_stats (
    rule_id UUID PRIMARY KEY REFERENCES recommendation(id) ON DELETE CASCADE,
    count INTEGER DEFAULT 0
);
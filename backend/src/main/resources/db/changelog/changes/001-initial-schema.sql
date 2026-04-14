-- liquibase formatted sql

-- changeset matkmiec:1
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- changeset matkmiec:2
CREATE TABLE backups(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL
);
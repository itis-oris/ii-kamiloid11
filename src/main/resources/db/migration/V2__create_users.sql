CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    bio           TEXT,
    avatar_url    VARCHAR(500),
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active     BOOLEAN   NOT NULL DEFAULT TRUE
);

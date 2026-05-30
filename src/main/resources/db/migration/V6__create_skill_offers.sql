CREATE TABLE skill_offers (
    id                BIGSERIAL PRIMARY KEY,
    title             VARCHAR(200) NOT NULL,
    description       TEXT,
    hours_per_session DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    hourly_rate       DOUBLE PRECISION,
    rate_currency     VARCHAR(3) DEFAULT 'EUR',
    max_students      INT       NOT NULL DEFAULT 1,
    is_active         BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP,
    owner_id          BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id          BIGINT    NOT NULL REFERENCES skills(id)
);

CREATE TABLE reviews (
    id          BIGSERIAL PRIMARY KEY,
    rating      INT       NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    exchange_id BIGINT    NOT NULL REFERENCES exchanges(id) ON DELETE CASCADE,
    author_id   BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_id   BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

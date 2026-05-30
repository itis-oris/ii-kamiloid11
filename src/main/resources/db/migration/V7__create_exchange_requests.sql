CREATE TABLE exchange_requests (
    id            BIGSERIAL PRIMARY KEY,
    message       TEXT,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP   NOT NULL DEFAULT NOW(),
    responded_at  TIMESTAMP,
    offer_id      BIGINT NOT NULL REFERENCES skill_offers(id) ON DELETE CASCADE,
    requester_id  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

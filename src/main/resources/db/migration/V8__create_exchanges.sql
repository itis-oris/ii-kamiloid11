CREATE TABLE exchanges (
    id                  BIGSERIAL PRIMARY KEY,
    scheduled_at        TIMESTAMP,
    duration_minutes    INT,
    notes               TEXT,
    completed_at        TIMESTAMP,
    exchange_request_id BIGINT NOT NULL UNIQUE REFERENCES exchange_requests(id) ON DELETE CASCADE
);

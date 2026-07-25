CREATE TABLE IF NOT EXISTS spendcontrol_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    started_at DATETIME NULL,
    last_event_at DATETIME NULL,
    total_events INT NOT NULL DEFAULT 0,
    ended_at DATETIME NULL
);

CREATE TABLE IF NOT EXISTS spendcontrol_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    ref_type VARCHAR(50) NULL,
    ref_id BIGINT NULL,
    payload_json TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS spendcontrol_daily (
    user_id BIGINT NOT NULL,
    ymd DATE NOT NULL,
    events_count INT NOT NULL DEFAULT 0,
    quiz_submits INT NOT NULL DEFAULT 0,
    wrong_reviews INT NOT NULL DEFAULT 0,
    wrong_review_done_count INT NOT NULL DEFAULT 0,
    last_event_at DATETIME NULL,
    PRIMARY KEY (user_id, ymd)
);
-- V15__create_study_sessions.sql
CREATE TABLE study_sessions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ended_at DATETIME NULL,
  total_events INT NOT NULL DEFAULT 0,
  last_event_at DATETIME NULL,
  PRIMARY KEY (id),
  INDEX idx_study_sessions_user_started (user_id, started_at),
  INDEX idx_study_sessions_user_last (user_id, last_event_at),
  CONSTRAINT fk_study_sessions_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

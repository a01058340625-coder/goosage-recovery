-- V16__create_study_events.sql
CREATE TABLE study_events (
  id BIGINT NOT NULL AUTO_INCREMENT,
  session_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  event_type VARCHAR(50) NOT NULL,   -- QUIZ_SUBMIT / WRONG_REVIEW / KNOWLEDGE_CREATE / QA_CREATE ...
  ref_type VARCHAR(50) NULL,         -- KNOWLEDGE / QUIZ_RESULT / QA ...
  ref_id BIGINT NULL,                -- knowledge_id, quiz_result_id 등
  payload_json JSON NULL,            -- 추가 정보(선택)
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_study_events_user_created (user_id, created_at),
  INDEX idx_study_events_session_created (session_id, created_at),
  INDEX idx_study_events_type_created (event_type, created_at),
  CONSTRAINT fk_study_events_session
    FOREIGN KEY (session_id) REFERENCES study_sessions(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_study_events_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

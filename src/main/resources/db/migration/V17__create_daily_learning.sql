-- V17__create_daily_learning.sql
CREATE TABLE daily_learning (
  user_id BIGINT NOT NULL,
  ymd DATE NOT NULL,                 -- 로컬 기준 날짜(Asia/Seoul)
  events_count INT NOT NULL DEFAULT 0,
  quiz_submits INT NOT NULL DEFAULT 0,
  wrong_reviews INT NOT NULL DEFAULT 0,
  last_event_at DATETIME NULL,
  PRIMARY KEY (user_id, ymd),
  INDEX idx_daily_learning_ymd (ymd),
  CONSTRAINT fk_daily_learning_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

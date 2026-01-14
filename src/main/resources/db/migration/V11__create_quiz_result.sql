CREATE TABLE IF NOT EXISTS quiz_result (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  knowledge_id BIGINT NOT NULL,
  question_idx INT NOT NULL,
  question_text TEXT,
  user_answer TEXT,
  correct TINYINT(1),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

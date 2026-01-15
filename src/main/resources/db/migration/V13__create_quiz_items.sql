CREATE TABLE IF NOT EXISTS quiz_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  knowledge_id BIGINT NOT NULL,
  no INT NOT NULL,
  question VARCHAR(500) NOT NULL,
  expected TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_quiz_items_knowledge_no (knowledge_id, no),
  INDEX idx_quiz_items_knowledge (knowledge_id)
);

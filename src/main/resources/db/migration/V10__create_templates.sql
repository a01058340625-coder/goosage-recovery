

CREATE TABLE IF NOT EXISTS templates (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  knowledge_id BIGINT NOT NULL,
  template_type VARCHAR(50) NOT NULL,
  result_text LONGTEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_templates_knowledge_type (knowledge_id, template_type),
  INDEX idx_templates_knowledge_id (knowledge_id),

  CONSTRAINT fk_templates_knowledge
    FOREIGN KEY (knowledge_id) REFERENCES knowledge(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);

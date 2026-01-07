-- V3__create_knowledge.sql
-- GooSage v0.1: 원문 지식(질문/답변/메모/링크 등) 저장 테이블

CREATE TABLE IF NOT EXISTS knowledge (
  id BIGINT NOT NULL AUTO_INCREMENT,
  type VARCHAR(30) NOT NULL,                 -- QNA, NOTE, LINK, SNIPPET ...
  title VARCHAR(200) NOT NULL,
  content LONGTEXT NOT NULL,                 -- 원문(질문/답변/메모 등)
  source VARCHAR(200) NULL,                  -- 출처(URL/파일경로 등)
  tags VARCHAR(500) NULL,                    -- 초기: "db,flyway,spring" 같은 콤마 문자열
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_knowledge_type (type),
  INDEX idx_knowledge_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

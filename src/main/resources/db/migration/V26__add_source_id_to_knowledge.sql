ALTER TABLE knowledge
  ADD COLUMN source_id BIGINT NULL;

CREATE INDEX idx_knowledge_source_id ON knowledge(source_id);

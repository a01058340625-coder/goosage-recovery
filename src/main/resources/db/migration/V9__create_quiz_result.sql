CREATE TABLE quiz_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    knowledge_id BIGINT NOT NULL,
    question_idx INT NOT NULL,
    question_text TEXT,
    user_answer TEXT,
    correct BOOLEAN,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_quiz_result_user_id ON quiz_result(user_id);
CREATE INDEX idx_quiz_result_knowledge_id ON quiz_result(knowledge_id);

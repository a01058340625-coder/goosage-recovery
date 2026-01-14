-- V9__create_quiz_results.sql
CREATE TABLE quiz_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    knowledge_id BIGINT NOT NULL,

    total_count INT NOT NULL,
    correct_count INT NOT NULL,
    score_percent INT NOT NULL,

    details_json JSON NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_quiz_results_knowledge_id (knowledge_id)
);

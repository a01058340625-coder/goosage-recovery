CREATE TABLE recovery_message_validations (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,

  original_message TEXT NOT NULL,

  expected_analyzable BOOLEAN NULL,
  expected_hold_reason VARCHAR(100) NULL,
  expected_signal_json JSON NULL,
  expected_pattern_type VARCHAR(100) NULL,
  expected_next_action_type VARCHAR(100) NULL,

  actual_analyzable BOOLEAN NOT NULL,
  actual_hold_reason VARCHAR(100) NULL,
  actual_signal_json JSON NULL,
  actual_pattern_type VARCHAR(100) NULL,
  actual_next_action_type VARCHAR(100) NULL,
  actual_recommended_action VARCHAR(150) NULL,

  validation_result VARCHAR(30) NOT NULL,
  mismatch_type VARCHAR(50) NULL,
  review_memo TEXT NULL,

  real_scenario_candidate BOOLEAN NOT NULL DEFAULT FALSE,
  virtual_user_candidate BOOLEAN NOT NULL DEFAULT FALSE,

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),

  INDEX idx_recovery_message_validations_user_created (
    user_id,
    created_at
  ),

  INDEX idx_recovery_message_validations_result_created (
    validation_result,
    created_at
  ),

  INDEX idx_recovery_message_validations_candidates (
    real_scenario_candidate,
    virtual_user_candidate,
    created_at
  ),

  CONSTRAINT fk_recovery_message_validations_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

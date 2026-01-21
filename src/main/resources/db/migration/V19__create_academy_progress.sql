CREATE TABLE IF NOT EXISTS academy_progress (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,         -- DONE (일단 이것만)
  completed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uq_progress_user_course_item (user_id, course_id, item_id),
  KEY idx_progress_user_course (user_id, course_id),
  KEY idx_progress_course (course_id),
  KEY idx_progress_item (item_id)
);

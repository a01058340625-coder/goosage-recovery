ALTER TABLE academy_progress
  ADD UNIQUE KEY uk_academy_progress_user_course_item (user_id, course_id, item_id);

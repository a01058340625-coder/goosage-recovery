INSERT INTO users (email, password_hash)
VALUES (
  'goosage@example.com',
  '$2a$10$1cRDL9p/fPfLLPgYb.pQSOAgVU5RnxFsShdmiQ.WWV5T6k/OxG4.S'
)
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash);
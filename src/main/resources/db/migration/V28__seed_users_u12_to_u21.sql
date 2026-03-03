-- V28__seed_users_u12_to_u21.sql
-- GooSage Persona accounts (u12~u21)
-- password = 1234 (BCrypt)
-- bcrypt(1234):
-- $2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW

INSERT INTO users (email, password_hash, created_at)
SELECT 'u12@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u12@goosage.test');

INSERT INTO users (email, password_hash, created_at)
SELECT 'u13@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u13@goosage.test');

INSERT INTO users (email, password_hash, created_at)
SELECT 'u14@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u14@goosage.test');

INSERT INTO users (email, password_hash, created_at)
SELECT 'u15@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u15@goosage.test');

INSERT INTO users (email, password_hash, created_at)
SELECT 'u16@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u16@goosage.test');

INSERT INTO users (email, password_hash, created_at)
SELECT 'u17@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u17@goosage.test');

INSERT INTO users (email, password_hash, created_at)
SELECT 'u18@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u18@goosage.test');

INSERT INTO users (email, password_hash, created_at)
SELECT 'u19@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u19@goosage.test');

INSERT INTO users (email, password_hash, created_at)
SELECT 'u20@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u20@goosage.test');

INSERT INTO users (email, password_hash, created_at)
SELECT 'u21@goosage.test',
       '$2a$10$zD0R9s/GOtftjSYmNMUNw.qsjflDPdjixxQu7FUViSZiF/pjqPwNW',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='u21@goosage.test');
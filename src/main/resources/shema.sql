ALTER TABLE exams MODIFY COLUMN teacher_id BIGINT NULL;
ALTER TABLE answer_records ADD CONSTRAINT fk_answer_record_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE scores ADD CONSTRAINT fk_score_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;
-- 先删除可能存在的旧管理员（避免冲突）
DELETE FROM users WHERE username = 'admin' AND role = 'ADMIN';

-- 插入新的可登录管理员（密码：admin123）
INSERT INTO users (username, password, role, email, created_at)
SELECT 'admin', '$2a$10$XpJ7M6z8uOwWv9zXy7QZ.e4rT5yH2uK3F6gH7j8kL9m0n1o2p3q4', 'ADMIN', 'admin@exam.com', NOW()
FROM (SELECT 1) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');
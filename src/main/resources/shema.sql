ALTER TABLE exams MODIFY COLUMN teacher_id BIGINT NULL;
ALTER TABLE answer_records ADD CONSTRAINT fk_answer_record_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE scores ADD CONSTRAINT fk_score_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;
INSERT INTO users (username, password, role, email, created_at)
SELECT 'admin', '$2a$04$mmZNAr3AAifcLL1yeD4VAOxRqZGGzsY51xXHxffG4aGB7fRQ05/YW', 'ADMIN', 'admin@exam.com', NOW()
FROM (SELECT 1) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE role = 'ADMIN');


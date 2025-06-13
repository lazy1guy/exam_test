ALTER TABLE exams MODIFY COLUMN teacher_id BIGINT NULL;
ALTER TABLE answer_records ADD CONSTRAINT fk_answer_record_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE scores ADD CONSTRAINT fk_score_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;



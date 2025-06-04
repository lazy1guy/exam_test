package com.exam.exam_test.repository;

import com.exam.exam_test.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExamId(Long examId);
    List<Question> findByHomeworkId(Long homeworkId);

    // 获取指定数目的题目
    @Query("SELECT q FROM Question q WHERE q.subject = :subject AND q.type = :type ORDER BY RAND() LIMIT :count")
    List<Question> findRandomBySubjectAndType(
            @Param("subject") String subject,
            @Param("type") String type,
            @Param("count") int count);
}

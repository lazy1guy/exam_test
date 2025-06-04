package com.exam.exam_test.repository;

import com.exam.exam_test.entity.AnswerRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AnswerRecordRepository extends JpaRepository<AnswerRecord, Long> {
    List<AnswerRecord> findByStudentIdAndExamId(Long studentId, Long examId);
    List<AnswerRecord> findByStudentIdAndHomeworkId(Long studentId, Long homeworkId);

    @EntityGraph(attributePaths = {"question"})
    List<AnswerRecord> findByStudentIdAndIsCorrectFalse(Long studentId);

    @EntityGraph(attributePaths = {"question"})
    List<AnswerRecord> findByStudentIdAndQuestionSubject(Long studentId, String subject);

    @EntityGraph(attributePaths = {"question"})
    List<AnswerRecord> findByStudentIdAndIsCorrectFalseAndMasteredFalse(Long studentId);

    @EntityGraph(attributePaths = {"question"})
    List<AnswerRecord> findByStudentIdAndQuestionId(Long studentId, Long questionId);
    // 返回错题
    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.isCorrect = false AND ar.question.subject = :subject")
    List<AnswerRecord> findErrorQuestionsBySubject(
            @Param("studentId") Long studentId,
            @Param("subject") String subject);
}

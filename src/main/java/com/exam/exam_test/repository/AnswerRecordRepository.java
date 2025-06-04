package com.exam.exam_test.repository;

import com.exam.exam_test.entity.AnswerRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
    List<AnswerRecord> findByStudentIdAndHomeworkIdAndQuestionId(Long studentId, Long examId, Long questionId);

    @EntityGraph(attributePaths = {"question"})
    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.question.id = :questionId ORDER BY ar.createdAt DESC")
    List<AnswerRecord> findByStudentIdAndQuestionId(Long studentId, Long questionId);

    // 删除学生作业草稿
    @Transactional
    @Modifying
    @Query("DELETE FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.homework.id = :homeworkId AND ar.isDraft = true")
    void deleteDraftBySyudentAndHomework(@Param("studentId") Long studentId,  @Param("homeworkId") Long homeworkId);

    // 返回错题
    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.isCorrect = false AND ar.question.subject = :subject")
    List<AnswerRecord> findErrorQuestionsBySubject(
            @Param("studentId") Long studentId,
            @Param("subject") String subject);
}

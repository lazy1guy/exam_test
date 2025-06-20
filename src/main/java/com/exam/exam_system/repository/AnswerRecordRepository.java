package com.exam.exam_system.repository;

import com.exam.exam_system.entity.AnswerRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface AnswerRecordRepository extends JpaRepository<AnswerRecord, Long> {
    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.exam.id = :examId")
    List<AnswerRecord> findByStudentIdAndExamId(Long studentId, Long examId);

    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.homework.id = :homeworkId")
    List<AnswerRecord> findByStudentIdAndHomeworkId(Long studentId, Long homeworkId);

    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.exam.id = :examId")
    List<AnswerRecord> findByExamId(Long examId);

    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.homework.id = :homeworkId")
    List<AnswerRecord> findByHomeworkId(Long homeworkId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AnswerRecord ar WHERE ar.id IN :ids")
    void deleteByIds(@Param("ids") Set<Long> ids);

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

    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.question.id IN :questionIds")
    List<AnswerRecord> findByStudentIdAndQuestionIds(@Param("studentId") Long studentId, @Param("questionIds") Set<Long> questionIds);

    // 删除学生作业草稿
    @Transactional
    @Modifying
    @Query("DELETE FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.homework.id = :homeworkId AND ar.isDraft = true")
    void deleteDraftByStudentAndHomework(@Param("studentId") Long studentId,  @Param("homeworkId") Long homeworkId);

    // 返回错题
    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.isCorrect = false AND ar.question.subject = :subject")
    List<AnswerRecord> findErrorQuestionsBySubject(
            @Param("studentId") Long studentId,
            @Param("subject") String subject);

    @Modifying
    @Transactional
    @Query("DELETE FROM AnswerRecord ar WHERE ar.student.id = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);

    long countByStudentIdAndIsCorrectFalseAndMasteredFalse(Long studentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AnswerRecord ar WHERE ar.question.id = :Id")
    void deleteByQuestionId(@Param("Id") Long Id);

    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.student.id = :studentId AND ar.homework.id = :homeworkId AND ar.isDraft = true")
    List<AnswerRecord> findDraftByStudentAndHomework(
            @Param("studentId") Long studentId,
            @Param("homeworkId") Long homeworkId);

    @Query("SELECT ar FROM AnswerRecord ar WHERE ar.homework.id = :homeworkId AND ar.isDraft = false")
    List<AnswerRecord> findByHomeworkIdAndIsDraftFalse(@Param("homeworkId") Long homeworkId);

    @Modifying
    @Query("DELETE FROM AnswerRecord ar WHERE ar.exam.id = :examId")
    void deleteByExamId(@Param("examId") Long examId);

    @Modifying
    @Query("DELETE FROM AnswerRecord ar WHERE ar.homework.id = :homeworkId")
    void deleteByHomeworkId(@Param("homeworkId") Long homeworkId);
}

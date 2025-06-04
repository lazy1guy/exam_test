package com.exam.exam_test.repository;

import com.exam.exam_test.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByStudentId(Long studentId);
    List<Score> findByExamId(Long examId);
    List<Score> findByHomeworkId(Long homeworkId);

    @Query("SELECT s FROM Score s WHERE s.student.id = :studentId AND s.exam.id = :examId")
    Optional<Score> findByStudentAndExam(
            @Param("studentId") Long studentId,
            @Param("examId") Long examId);

    @Query("SELECT s FROM Score s WHERE s.student.id = :studentId AND s.homework.id = :homeworkId")
    Optional<Score> findByStudentAndHomework(
            @Param("studentId") Long studentId,
            @Param("homeworkId") Long homeworkId);
}

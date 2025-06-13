package com.exam.exam_system.repository;

import com.exam.exam_system.entity.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    // 添加分页支持
    Page<Score> findByStudentId(Long studentId, Pageable pageable);

    List<Score> findByStudentId(Long studentId);
    List<Score> findByExamId(Long examId);
    List<Score> findByHomeworkId(Long homeworkId);

    // 添加批量查询
    @Query("SELECT s FROM Score s WHERE s.id IN :ids")
    List<Score> findByIds(@Param("ids") Set<Long> ids);

    // 添加IN查询优化, 批量查询
    @Query("SELECT s FROM Score s WHERE s.student.id = :studentId AND s.exam.id IN :examIds")
    List<Score> findByStudentAndExams(
            @Param("studentId") Long studentId,
            @Param("examIds") Set<Long> examIds);

    // 单个查询
    @Query("SELECT s FROM Score s WHERE s.student.id = :studentId AND s.exam.id = :examId")
    Optional<Score> findByStudentAndExam(
            @Param("studentId") Long studentId,
            @Param("examId") Long examId);

    // 批量查询
    @Query("SELECT s FROM Score s WHERE s.student.id = :studentId AND s.homework.id IN :homeworkIds")
    List<Score> findByStudentAndHomeworks(
            @Param("studentId") Long studentId,
            @Param("homeworkIds") Set<Long> homeworkIds);

    // 单个查询
    @Query("SELECT s FROM Score s WHERE s.student.id = :studentId AND s.homework.id = :homeworkId")
    Optional<Score> findByStudentAndHomework(
            @Param("studentId") Long studentId,
            @Param("homeworkId") Long homeworkId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Score s WHERE s.student.id = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);

    // 通过 examId删除
    @Modifying
    @Query("DELETE FROM Score s WHERE s.exam.id = :examId")
    void deleteByExamId(@Param("examId") Long examId);

    // 通过homeworkId删除
    @Modifying
    @Query("DELETE FROM Score s WHERE s.homework.id = :homeworkId")
    void deleteByHomeworkId(@Param("homeworkId") Long homeworkId);
}

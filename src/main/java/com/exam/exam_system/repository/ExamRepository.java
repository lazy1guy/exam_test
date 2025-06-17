package com.exam.exam_system.repository;

import com.exam.exam_system.entity.Exam;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Long>{

    // 添加分页支持
    Page<Exam> findByTeacherId(Long id, Pageable pageable);

    List<Exam> findByTeacherId(Long id);
    List<Exam> findByStartTimeAfter(LocalDateTime startTime);
    List<Exam> findByStartTimeBefore(LocalDateTime endTime);

    // 查询当前阶段活跃的考试
    @QueryHints(value = {
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "examsCache")
    })
    @Query("SELECT e FROM Exam e WHERE e.endTime > :now AND e.startTime < :now ORDER BY e.startTime DESC")
    List<Exam> findOngoingExams(@Param("now") LocalDateTime now);

    // 按教师ID查询当前活跃的考试
    @Query("SELECT e FROM Exam e WHERE e.teacher.id = :teacherId AND e.endTime > :now")
    Page<Exam> findActiveExamsByTeacher(@Param("teacherId") Long teacherId, @Param("now") LocalDateTime now, Pageable pageable);

    // 获取指定id的考试
    @Query("SELECT e FROM Exam e WHERE e.id IN :ids")
    List<Exam> findByIds(@Param("ids") Set<Long> ids);

    // 获取指定科目的考试
    @Query("SELECT e FROM Exam e WHERE e.subject = :subject")
    List<Exam> findSubject(@Param("subject") String subject);

    @Modifying
    @Transactional
    @Query("UPDATE Exam e SET e.teacher = NULL WHERE e.teacher.id = :teacherId")
    void nullifyTeacherIdByUserId(@Param("teacherId") Long teacherId);

    @Query("SELECT e FROM Exam e WHERE e.title = :title")
    Optional<Exam> findByTitle(@Param("title") String title);
}

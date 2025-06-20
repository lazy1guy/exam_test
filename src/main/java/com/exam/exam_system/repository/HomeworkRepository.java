package com.exam.exam_system.repository;

import com.exam.exam_system.entity.Exam;
import com.exam.exam_system.entity.Homework;
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

public interface HomeworkRepository extends JpaRepository<Homework, Long> {

    // 添加分页支持
    Page<Homework> findByTeacherId(Long teacherId, Pageable pageable);

    List<Homework> findByTeacherId(Long teacherId);
    List<Homework> findByDeadlineAfter(LocalDateTime deadline);

    // 添加缓存提示
    @QueryHints(value = {
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "homeworksCache")
    })
    @Query("SELECT h FROM Homework h WHERE h.deadline > :now ORDER BY h.deadline ASC")
    List<Homework> findActiveHomeworks(@Param("now") LocalDateTime now);

    // 添加分页查询
    @Query("SELECT h FROM Homework h WHERE h.teacher.id = :teacherId AND h.deadline > :now")
    Page<Homework> findActiveHomeworksByTeacher(@Param("teacherId") Long teacherId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT h FROM Homework h WHERE h.subject = :subject")
    List<Homework> findBySubject(@Param("subject") String subject);

    // 添加IN查询优化
    @Query("SELECT h FROM Homework h WHERE h.id IN :ids")
    List<Homework> findByIds(@Param("ids") Set<Long> ids);

    // 获取相应教师的考试
    @Query("SELECT h FROM Homework h WHERE h.teacher.username = :teacherName")
    List<Homework> findByTeacherName(@Param("teacherName") String teacherName);

    @Modifying
    @Transactional
    @Query("UPDATE Homework h SET h.teacher = NULL WHERE h.teacher.id = :teacherId")
    void nullifyTeacherIdByUserId(@Param("teacherId") Long teacherId);

    @Query("SELECT h FROM Homework h WHERE h.title = :title")
    Optional<Homework> findByTitle(@Param("title") String title);

    @Modifying
    @Query("DELETE FROM Homework h WHERE h.teacher.id = :id")
    void deleteByTeacherId(@Param("id") Long id);
}

package com.exam.exam_system.repository;

import com.exam.exam_system.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findByTeacherId(Long teacherId);
    List<Homework> findByDeadlineAfter(LocalDateTime deadline);

    @Query("SELECT h FROM Homework h WHERE h.deadline > :now")
    List<Homework> findActiveHomeworks(@Param("now") LocalDateTime now);

    @Query("SELECT h FROM Homework h WHERE h.subject = :subject")
    List<Homework> findBySubject(@Param("subject") String subject);
}

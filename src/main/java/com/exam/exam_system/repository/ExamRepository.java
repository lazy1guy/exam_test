package com.exam.exam_system.repository;

import com.exam.exam_system.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long>{
    List<Exam> findByTeacherId(Long id);
    List<Exam> findByStartTimeAfter(LocalDateTime startTime);
    List<Exam> findByStartTimeBefore(LocalDateTime endTime);

    @Query("SELECT e FROM Exam e WHERE e.endTime > :now AND e.startTime < :now")
    List<Exam> findOngoingExams(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Exam e WHERE e.subject = :subject")
    List<Exam> findSubject(@Param("subject") String subject);

}

package com.exam.exam_system.repository;

import com.exam.exam_system.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExamId(Long examId);

    // 添加IN查询优化
    @Query("SELECT q FROM Question q WHERE q.exam.id IN :examIds")
    List<Question> findByExamIds(@Param("examIds") Set<Long> examIds);

    List<Question> findByHomeworkId(Long homeworkId);

    @Query("SELECT q FROM Question q WHERE q.homework.id IN :homeworkIds")
    List<Question> findByHomeworkIds(@Param("homeworkIds") Set<Long> homeworkIds);

    // 添加批量查询
    @Query("SELECT q FROM Question q WHERE q.id IN :ids")
    List<Question> findByIds(@Param("ids") Set<Long> ids);

    // 获取指定数目的题目
    @Query(value = "SELECT * FROM question WHERE subject = :subject AND type = :type ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Question> findRandomBySubjectAndType(
            @Param("subject") String subject,
            @Param("type") String type,
            @Param("count") int count);
}

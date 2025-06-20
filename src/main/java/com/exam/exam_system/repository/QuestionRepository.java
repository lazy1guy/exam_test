package com.exam.exam_system.repository;

import com.exam.exam_system.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    // 通过 examId删除
    @Modifying
    @Query("DELETE FROM Question q WHERE q.exam.id = :examId")
    void deleteByExamId(@Param("examId") Long examId);

    // 通过homeworkId删除
    @Modifying
    @Query("DELETE FROM Question q WHERE q.homework.id = :homeworkId")
    void deleteByHomeworkId(@Param("homeworkId") Long homeworkId);

    @Query("SELECT q FROM Question q WHERE q.content = :content")
    Optional<Question> findByContent(@Param("content") String content);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.exam.teacher.id = :id")
    void deleteByExamTeacherId(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.homework.teacher.id = :id")
    void deleteByHomeworkTeacherId(@Param("id") Long id);
}

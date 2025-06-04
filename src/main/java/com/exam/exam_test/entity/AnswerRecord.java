// 答题记录实体类
package com.exam.exam_test.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "answer_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "homework_id")
    private Homework homework;

    @Column(nullable = false)
    private String answer;

    private Boolean isCorrect;
    private Boolean isDraft;    // 是否为草稿（对作业而言）
    private Integer score; // 该题得分

    @Column(columnDefinition = "TEXT")
    private String note; // 错题笔记

    @Column(name = "is_mastered", columnDefinition = "boolean default false")
    private boolean mastered = false; // 是否已掌握

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

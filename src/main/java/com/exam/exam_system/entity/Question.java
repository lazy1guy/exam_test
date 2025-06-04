// 题目实体
package com.exam.exam_system.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String type; // SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER

    @Column(columnDefinition = "TEXT")
    private String options; // JSON格式存储选项

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private Integer score;

    private String subject; // 科目

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "homework_id")
    private Homework homework;

    public Question(Long questionId) {
        this.id = questionId;
    }
}

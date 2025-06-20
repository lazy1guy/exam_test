// 题目实体
package com.exam.exam_system.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Question.class
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "question", indexes = {
        @Index(name = "idx_question_exam", columnList = "exam_id"),
        @Index(name = "idx_question_homework", columnList = "homework_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String type; // single, multiple, short, judge

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

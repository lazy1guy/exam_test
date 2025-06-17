// 答题记录实体类
package com.exam.exam_system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = AnswerRecord.class
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "answer_record", indexes = {
        @Index(name = "idx_answerrecord_student", columnList = "student_id"),
        @Index(name = "idx_answerrecord_exam", columnList = "exam_id"),
        @Index(name = "idx_answerrecord_homework", columnList = "homework_id"),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRecord implements Serializable
{
    private static final long serialVersionUID = 1L;
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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

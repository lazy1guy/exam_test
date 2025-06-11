package com.exam.exam_system.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "score", indexes = {
        @Index(name = "idx_score_student", columnList = "student_id"),
        @Index(name = "idx_score_exam", columnList = "exam_id"),
        @Index(name = "idx_score_homework", columnList = "homework_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "homework_id")
    private Homework homework;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer totalScore;

    private String status; // PASSED/FAILED, COMPLETED/LATE

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

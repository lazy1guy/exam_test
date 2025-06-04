package com.exam.exam_system.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "exams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Exam(Long examId){
        this.id = examId;
    }

    @Column(nullable = false)
    private String title; // 考试标签

    private String description; // 考试信息描述
    private String subject; // 科目

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer duration; //考试时间（分钟）

    @Column(nullable = false)
    private Integer totalScore;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    private List<Question> questions;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private boolean published; // 成绩是否已发布

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}

package com.exam.exam_test.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "homeworks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Homework{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Homework(Long homeworkId){
        this.id = homeworkId;
    }

    @Column(nullable = false)
    private String title; // 作业标签

    private String description; // 作业信息描述
    private String subject; // 科目

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false)
    private LocalDateTime deadline; //  作业截止时间

    @Column(nullable = false)
    private Integer totalScore;

    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL)
    private List<Question> questions;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}

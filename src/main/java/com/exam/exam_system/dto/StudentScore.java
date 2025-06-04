package com.exam.exam_system.dto;

import lombok.Data;

@Data
public class StudentScore {
    private Long studentId;
    private String studentName;
    private Integer score;
    private Integer totalScore;
    private String status;
}

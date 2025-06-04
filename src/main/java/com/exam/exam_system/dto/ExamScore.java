package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Exam;
import lombok.Data;

@Data
public class ExamScore {
    private Exam exam;
    private Integer score;
    private Integer totalScore;
    private String status;
}

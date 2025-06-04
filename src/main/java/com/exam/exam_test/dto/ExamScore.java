package com.exam.exam_test.dto;

import com.exam.exam_test.entity.Exam;
import lombok.Data;

@Data
public class ExamScore {
    private Exam exam;
    private Integer score;
    private Integer totalScore;
    private String status;
}

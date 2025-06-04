package com.exam.exam_test.dto;

import lombok.Data;

@Data
public class LearningStatistics {
    private Integer totalExams;
    private Integer totalHomeworks;
    private double avgExamScore;
    private double avgHomeworkScore;
    private int errorCount;
}

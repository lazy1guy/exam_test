package com.exam.exam_test.dto;

import lombok.Data;

@Data
public class ScoreSummary {
    private Integer totalExams;
    private Integer totalHomeworks;
    private double avgExamScore;
    private double avgHomeworkScore;
    private Integer errorCount;
}

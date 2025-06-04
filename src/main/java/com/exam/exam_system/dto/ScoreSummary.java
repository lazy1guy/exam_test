package com.exam.exam_system.dto;

import lombok.Data;

@Data
public class ScoreSummary {
    private Integer totalExams;
    private Integer totalHomeworks;
    private double avgExamScore;
    private double avgHomeworkScore;
    private Integer errorCount;
}

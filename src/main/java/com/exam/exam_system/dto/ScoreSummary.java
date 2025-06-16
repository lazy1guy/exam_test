package com.exam.exam_system.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScoreSummary implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Integer totalExams;
    private Integer totalHomeworks;
    private double avgExamScore;
    private double avgHomeworkScore;
    private Integer errorCount;
}

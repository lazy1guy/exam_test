package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Homework;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class HomeworkResults {
    private HomeworkDTO homework;
    private double avgScore;
    private double completionRate;
    private List<StudentScore> studentScores;
    private Integer maxScore;
    private Integer minScore;
    private Map<Long, Double> questionAvgScores;
    private Map<String, Long> scoreDistribution;
}

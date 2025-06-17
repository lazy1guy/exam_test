package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Exam;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ExamResults {
    private ExamDTO exam;
    private double avgScore;
    private double passRate;
    private List<StudentScore> studentScores;
    private Integer maxScore;
    private Integer minScore;
    private Map<Long, Double> questionAvgScores;
    private Map<String, Long> scoreDistribution;
}

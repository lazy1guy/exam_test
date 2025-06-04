package com.exam.exam_test.dto;

import com.exam.exam_test.entity.Exam;

import lombok.Data;
import java.util.List;

@Data
public class ExamResults {
    private Exam exam;
    private double avgScore;
    private double passRate;
    private List<StudentScore> studentScores;
}

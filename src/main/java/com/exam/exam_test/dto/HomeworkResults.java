package com.exam.exam_test.dto;

import com.exam.exam_test.entity.Homework;

import lombok.Data;
import java.util.List;

@Data
public class HomeworkResults {
    private Homework homework;
    private double avgScore;
    private double completionRate;
    private List<StudentScore> studentScores;
}

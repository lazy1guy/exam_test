package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Homework;

import lombok.Data;
import java.util.List;

@Data
public class HomeworkResults {
    private Homework homework;
    private double avgScore;
    private double completionRate;
    private List<StudentScore> studentScores;
}

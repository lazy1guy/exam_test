package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Homework;
import lombok.Data;

@Data
public class HomeworkScore {
    private Homework homework;
    private Integer score;
    private Integer totalScore;
    private String status;
}

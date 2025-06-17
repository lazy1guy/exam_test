package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Homework;
import lombok.Data;

import java.io.Serializable;

@Data
public class HomeworkScore implements Serializable
{
    private static final long serialVersionUID = 1L;

    private HomeworkDTO homework;
    private Integer score;
    private Integer totalScore;
    private String status;
}

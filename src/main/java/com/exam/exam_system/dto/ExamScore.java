package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Exam;
import lombok.Data;

import java.io.Serializable;

@Data
public class ExamScore implements Serializable
{
    private static final long serialVersionUID = 1L;

    private ExamDTO exam;
    private Integer score;
    private Integer totalScore;
    private String status;
}

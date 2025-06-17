package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Exam;
import com.exam.exam_system.entity.Question;

import jakarta.persistence.Column;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamDetail implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Boolean hasTaken;
    private ExamDTO exam;
    private List<QuestionDTO> questions;
    private String subject;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration; //考试时间（分钟）
}

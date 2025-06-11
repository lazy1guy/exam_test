package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Exam;
import com.exam.exam_system.entity.Question;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamDetail {
    private Boolean hasTaken;
    private Exam exam;
    private List<Question> questions;
    private string subject;
    private string title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration; //考试时间（分钟）
}

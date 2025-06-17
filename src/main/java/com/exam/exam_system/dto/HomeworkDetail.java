package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Homework;
import com.exam.exam_system.entity.Question;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HomeworkDetail implements Serializable
{
    private static final long serialVersionUID = 1L;

    private HomeworkDTO homework;
    private List<QuestionDTO> questions;
    private Boolean hasSubmitted;
    private String subject;
    private String title;
    private LocalDateTime deadline;
}

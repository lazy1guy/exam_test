package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Exam;
import com.exam.exam_system.entity.Question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExamPaper implements Serializable
{
    private static final long serialVersionUID = 1L;

    private ExamDTO exam;
    private List<QuestionDTO> questions;
    private long remainingTime;
}

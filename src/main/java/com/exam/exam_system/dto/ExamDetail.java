package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Exam;
import com.exam.exam_system.entity.Question;

import lombok.Data;
import java.util.List;

@Data
public class ExamDetail {
    private Boolean hasTaken;
    private Exam exam;
    private List<Question> questions;
}

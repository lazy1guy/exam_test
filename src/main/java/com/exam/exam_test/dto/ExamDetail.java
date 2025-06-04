package com.exam.exam_test.dto;

import com.exam.exam_test.entity.Exam;
import com.exam.exam_test.entity.Question;

import lombok.Data;
import java.util.List;

@Data
public class ExamDetail {
    private Boolean hasTaken;
    private Exam exam;
    private List<Question> questions;
}

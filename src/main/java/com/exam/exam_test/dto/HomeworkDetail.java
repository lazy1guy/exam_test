package com.exam.exam_test.dto;

import com.exam.exam_test.entity.Homework;
import com.exam.exam_test.entity.Question;

import lombok.Data;
import java.util.List;

@Data
public class HomeworkDetail {
    private Homework homework;
    private List<Question> questions;
    private Boolean hasSubmitted;
}

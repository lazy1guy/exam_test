package com.exam.exam_test.dto;

import com.exam.exam_test.entity.Homework;
import com.exam.exam_test.entity.Question;

import lombok.Data;
import java.util.List;

@Data
public class HomeworkPaper {
    private Homework homework;
    private List<Question> questions;
}

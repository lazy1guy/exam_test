package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Homework;
import com.exam.exam_system.entity.Question;

import lombok.Data;
import java.util.List;

@Data
public class HomeworkPaper {
    private Homework homework;
    private List<Question> questions;
}

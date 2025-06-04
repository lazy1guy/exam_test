package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Homework;
import com.exam.exam_system.entity.Question;

import lombok.Data;
import java.util.List;

@Data
public class HomeworkDetail {
    private Homework homework;
    private List<Question> questions;
    private Boolean hasSubmitted;
}

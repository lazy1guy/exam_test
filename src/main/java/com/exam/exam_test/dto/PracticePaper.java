package com.exam.exam_test.dto;

import lombok.Data;
import com.exam.exam_test.entity.Question;

import java.util.List;

@Data
public class PracticePaper {
    private List<Question> questions;
}

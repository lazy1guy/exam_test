package com.exam.exam_system.dto;

import lombok.Data;
import com.exam.exam_system.entity.Question;

import java.util.List;

@Data
public class PracticePaper {
    private List<QuestionDTO> questions;
}

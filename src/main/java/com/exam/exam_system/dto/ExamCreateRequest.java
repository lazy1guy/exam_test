package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Question;

import java.time.LocalDateTime;
import lombok.Data;
import java.util.List;

@Data
public class ExamCreateRequest {
    private String title;   // 试题Title;
    private String description; // 试题描述
    private Long teacherId;   // 创建试题的教师id
    private String subject; //科目
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private List<Question> questions;
}

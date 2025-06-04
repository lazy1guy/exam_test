package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Question;

import java.time.LocalDateTime;
import lombok.Data;
import java.util.List;

@Data
public class HomeworkCreateRequest {
    private String title;   // 作业Title;
    private String description; // 作业描述
    private Long teacherId;   // 创建作业的教师id
    private String subject; //科目
    private LocalDateTime deadline;
    private List<Question> questions;
}

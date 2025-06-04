package com.exam.exam_system.dto;

import lombok.Data;
import com.exam.exam_system.entity.Question;

import java.time.LocalDateTime;


@Data
public class ErrorQuestion {
    private Long id;
    private Question question;
    private String userAnswer;
    private String correctAnswer;
    private String source; // 来源：考试、作业等
    private LocalDateTime errorTime;
    private String note;
    private boolean mastered; // 是否已掌握
}

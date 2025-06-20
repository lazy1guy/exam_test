package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Question;
import lombok.Data;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class QuestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String content;
    private String type;
    private String options;
    private Integer score;
    private String subject;
    private String answer;
    private String draftAnswer; // 草稿答案

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.content = question.getContent();
        this.type = question.getType();
        this.score = question.getScore();
        this.subject = question.getSubject();
        this.answer = question.getAnswer();

        // 解析选项字符串为列表
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            this.options = question.getOptions();
        }
    }

    public QuestionDTO() {}

    public void setDraftAnswer(String s) {
        this.draftAnswer = s;
    }
}

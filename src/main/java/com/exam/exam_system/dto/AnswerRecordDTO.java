package com.exam.exam_system.dto;

import com.exam.exam_system.entity.AnswerRecord;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AnswerRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private UserDTO student;
    private QuestionDTO question;
    private String answer;
    private Boolean isCorrect;
    private Boolean isDraft;
    private Integer score;
    private String note;
    private boolean mastered;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    public AnswerRecordDTO(AnswerRecord record) {
        this.id = record.getId();
        if (record.getStudent() != null) {
            this.student = new UserDTO(record.getStudent());
        }
        if (record.getQuestion() != null) {
            this.question = new QuestionDTO(record.getQuestion());
        }
        this.answer = record.getAnswer();
        this.isCorrect = record.getIsCorrect();
        this.isDraft = record.getIsDraft();
        this.score = record.getScore();
        this.note = record.getNote();
        this.mastered = record.isMastered();
        this.createdAt = record.getCreatedAt();
    }

    public AnswerRecordDTO() {}
}

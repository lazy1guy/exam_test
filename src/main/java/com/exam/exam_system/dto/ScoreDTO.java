package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Score;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ScoreDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private UserDTO student;
    private ExamDTO exam;
    private HomeworkDTO homework;
    private Integer score;
    private Integer totalScore;
    private String status;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    public ScoreDTO(Score score) {
        this.id = score.getId();
        if (score.getStudent() != null) {
            this.student = new UserDTO(score.getStudent());
        }
        if (score.getExam() != null) {
            this.exam = new ExamDTO(score.getExam());
        }
        if (score.getHomework() != null) {
            this.homework = new HomeworkDTO(score.getHomework());
        }
        this.score = score.getScore();
        this.totalScore = score.getTotalScore();
        this.status = score.getStatus();
        this.createdAt = score.getCreatedAt();
    }

    public ScoreDTO() {}
}

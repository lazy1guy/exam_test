package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Homework;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class HomeworkDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String description;
    private String subject;
    private UserDTO teacher;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;
    private Integer totalScore;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    public HomeworkDTO(Homework homework) {
        this.id = homework.getId();
        this.title = homework.getTitle();
        this.description = homework.getDescription();
        this.subject = homework.getSubject();
        if (homework.getTeacher() != null) {
            this.teacher = new UserDTO(homework.getTeacher());
        }
        this.deadline = homework.getDeadline();
        this.totalScore = homework.getTotalScore();
        this.createdAt = homework.getCreatedAt();
    }

    public HomeworkDTO() {}
}

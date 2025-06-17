package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Exam;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ExamDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String description;
    private String subject;
    private UserDTO teacher;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    private Integer duration;
    private Integer totalScore;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
    private boolean published;

    public ExamDTO(Exam exam) {
        this.id = exam.getId();
        this.title = exam.getTitle();
        this.description = exam.getDescription();
        this.subject = exam.getSubject();
        if (exam.getTeacher() != null) {
            this.teacher = new UserDTO(exam.getTeacher());
        }
        this.startTime = exam.getStartTime();
        this.endTime = exam.getEndTime();
        this.duration = exam.getDuration();
        this.totalScore = exam.getTotalScore();
        this.createdAt = exam.getCreatedAt();
        this.published = exam.isPublished();
    }

    public ExamDTO() {}
}

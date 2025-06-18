package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Homework;
import com.exam.exam_system.entity.Question;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HomeworkDetail implements Serializable
{
    private static final long serialVersionUID = 1L;

    private HomeworkDTO homework;
    private List<QuestionDTO> questions;
    private Boolean hasSubmitted;
    private String subject;
    private String title;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;
}

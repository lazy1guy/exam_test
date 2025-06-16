package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Score;
import com.exam.exam_system.entity.AnswerRecord;
import com.exam.exam_system.entity.Homework;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HomeworkScoreDetail implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Homework homework;
    private Score score;
    private List<AnswerRecord> answers;
}

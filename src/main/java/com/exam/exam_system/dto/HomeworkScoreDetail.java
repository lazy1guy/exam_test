package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Score;
import com.exam.exam_system.entity.AnswerRecord;
import com.exam.exam_system.entity.Homework;
import lombok.Data;
import java.util.List;

@Data
public class HomeworkScoreDetail {
    private Homework homework;
    private Score score;
    private List<AnswerRecord> answers;
}

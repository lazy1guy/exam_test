package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Score;
import com.exam.exam_system.entity.AnswerRecord;
import com.exam.exam_system.entity.Exam;
import lombok.Data;
import java.util.List;

@Data
public class ExamScoreDetail {
    private Exam exam;
    private Score score;
    private List<AnswerRecord> answers;
}

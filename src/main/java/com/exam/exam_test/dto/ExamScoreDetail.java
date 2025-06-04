package com.exam.exam_test.dto;

import com.exam.exam_test.entity.Score;
import com.exam.exam_test.entity.AnswerRecord;
import com.exam.exam_test.entity.Exam;
import lombok.Data;
import java.util.List;

@Data
public class ExamScoreDetail {
    private Exam exam;
    private Score score;
    private List<AnswerRecord> answers;
}

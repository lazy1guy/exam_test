package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Score;
import com.exam.exam_system.entity.AnswerRecord;
import com.exam.exam_system.entity.Exam;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExamScoreDetail implements Serializable
{
    private static final long serialVersionUID = 1L;

    private ExamDTO exam;
    private ScoreDTO score;
    private List<AnswerRecordDTO> answers;
}

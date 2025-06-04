package com.exam.exam_test.service;

import com.exam.exam_test.entity.AnswerRecord;
import com.exam.exam_test.entity.Question;
import com.exam.exam_test.repository.AnswerRecordRepository;
import com.exam.exam_test.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static java.util.stream.Collectors.*;

@Service
public class AnswerService {
    private final AnswerRecordRepository answerRecordRepository;
    private final QuestionRepository questionRepository;

    public AnswerService(AnswerRecordRepository answerRecordRepository,
                         QuestionRepository questionRepository) {
        this.answerRecordRepository = answerRecordRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public void submitExamAnswers(Long studentId, Long examId, List<AnswerRecord> answers) {
        for (AnswerRecord answer : answers) {
            answer.setStudentId(studentId);
            answer.setExamId(examId);

            // 自动批改客观题
            if (isAutoGradeQuestion(answer.getQuestion().getType())) {
                Question question = questionRepository.findById(answer.getQuestion().getId())
                        .orElseThrow(() -> new RuntimeException("题目不存在"));

                answer.setIsCorrect(question.getAnswer().equals(answer.getAnswer()));
            }

            answerRecordRepository.save(answer);
        }

        // 计算并保存成绩
        calculateAndSaveScore(studentId, examId, null);
    }

    @Transactional
    public void submitHomeworkAnswers(Long studentId, Long homeworkId, List<AnswerRecord> answers) {
        for (AnswerRecord answer : answers) {
            answer.setStudentId(studentId);
            answer.setHomeworkId(homeworkId);

            // 自动批改客观题
            if (isAutoGradeQuestion(answer.getQuestion().getType())) {
                Question question = questionRepository.findById(answer.getQuestion().getId())
                        .orElseThrow(() -> new RuntimeException("题目不存在"));

                answer.setIsCorrect(question.getAnswer().equals(answer.getAnswer()));
            }

            answerRecordRepository.save(answer);
        }

        // 计算并保存成绩
        calculateAndSaveScore(studentId, null, homeworkId);
    }

    @Transactional(readOnly = true)
    public List<Question> getWrongQuestions(Long studentId, Long examId, Long homeworkId) {
        // 查询相关答案记录
        List<AnswerRecord> wrongAnswers;
        if (examId != null) {
            wrongAnswers = answerRecordRepository.findByStudentIdAndExamIdAndIsCorrectFalse(studentId, examId);
        } else if (homeworkId != null) {
            wrongAnswers = answerRecordRepository.findByStudentIdAndHomeworkIdAndIsCorrectFalse(studentId, homeworkId);
        } else {
            throw new IllegalArgumentException("Either examId or homeworkId must be provided.");
        }

        // 获取错题的题目ID列表
        List<Long> questionIds = wrongAnswers.stream()
                .map(answer -> answer.getQuestion().getId()
                )
                .collect(Collectors.toList());

        // 查询题目信息
        return questionRepository.findAllById(questionIds);
    }


    private boolean isAutoGradeQuestion(String type) {
        return "SINGLE_CHOICE".equals(type) ||
                "MULTIPLE_CHOICE".equals(type) ||
                "TRUE_FALSE".equals(type);
    }

    private void calculateAndSaveScore(Long studentId, Long examId, Long homeworkId) {
        // 实现成绩计算逻辑
        // ...
    }
}

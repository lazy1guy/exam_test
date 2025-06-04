// 成绩服务
package com.exam.exam_test.service;

import com.exam.exam_test.entity.*;
import com.exam.exam_test.dto.*;
import com.exam.exam_test.repository.ExamRepository;
import com.exam.exam_test.repository.ScoreRepository;
import com.exam.exam_test.repository.UserRepository;
import com.exam.exam_test.repository.AnswerRecordRepository;
import com.exam.exam_test.repository.HomeworkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    private final HomeworkRepository homeworkRepository;
    private final ExamRepository examRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;

    public ScoreService(HomeworkRepository homeworkRepository, ExamRepository examRepository, AnswerRecordRepository answerRecordRepository, ScoreRepository scoreRepository, UserRepository userRepository) {
        this.homeworkRepository = homeworkRepository;
        this.examRepository = examRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
    }

    public ScoreSummary getScoreSummary(Long userId) {
        List<Score> scores = scoreRepository.findByStudentId(userId);

        ScoreSummary summary = new ScoreSummary();
        summary.setTotalExams((int) scores.stream().filter(s -> s.getExam() != null).count());
        summary.setTotalHomeworks((int) scores.stream().filter(s -> s.getHomework() != null).count());

        double examAvg = scores.stream()
                .filter(s -> s.getExam() != null)
                .mapToInt(Score::getScore)
                .average()
                .orElse(0.0);
        summary.setAvgExamScore(examAvg);

        double homeworkAvg = scores.stream()
                .filter(s -> s.getHomework() != null)
                .mapToInt(Score::getScore)
                .average()
                .orElse(0.0);
        summary.setAvgHomeworkScore(homeworkAvg);

        long errorCount = scores.stream()
                .filter(s -> "FAILED".equals(s.getStatus()))
                .count();
        summary.setErrorCount((int) errorCount);

        return summary;
    }

    public List<ExamScore> getExamScores(Long userId) {
        return scoreRepository.findByStudentId(userId).stream()
                .filter(s -> s.getExam() != null)
                .map(s -> {
                    ExamScore es = new ExamScore();
                    es.setExam(s.getExam());
                    es.setScore(s.getScore());
                    es.setTotalScore(s.getTotalScore());
                    es.setStatus(s.getStatus());
                    return es;
                })
                .collect(Collectors.toList());
    }

    public List<HomeworkScore> getHomeworkScores(Long userId) {
        return scoreRepository.findByStudentId(userId).stream()
                .filter(s -> s.getHomework() != null)
                .map(s -> {
                    HomeworkScore hs = new HomeworkScore();
                    hs.setHomework(s.getHomework());
                    hs.setScore(s.getScore());
                    hs.setTotalScore(s.getTotalScore());
                    hs.setStatus(s.getStatus());
                    return hs;
                })
                .collect(Collectors.toList());
    }

    public ExamScoreDetail getExamScoreDetail(Long examId, Long userId) {
        Score score = scoreRepository.findByStudentAndExam(userId, examId)
                .orElseThrow(() -> new RuntimeException("成绩不存在"));

        List<AnswerRecord> answers = answerRecordRepository.findByStudentIdAndExamId(userId, examId);

        ExamScoreDetail detail = new ExamScoreDetail();
        detail.setScore(score);
        detail.setAnswers(answers);
        detail.setExam(examRepository.findById(examId).orElse(null));

        return detail;
    }

    public HomeworkScoreDetail getHomeworkScoreDetail(Long homeworkId, Long userId) {
        Score score = scoreRepository.findByStudentAndHomework(userId, homeworkId)
                .orElseThrow(() -> new RuntimeException("成绩不存在"));

        List<AnswerRecord> answers = answerRecordRepository.findByStudentIdAndHomeworkId(userId, homeworkId);

        HomeworkScoreDetail detail = new HomeworkScoreDetail();
        detail.setScore(score);
        detail.setAnswers(answers);
        detail.setHomework(homeworkRepository.findById(homeworkId).orElse(null));

        return detail;
    }
}

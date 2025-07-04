// 成绩服务
package com.exam.exam_system.service;

import com.exam.exam_system.dto.*;
import com.exam.exam_system.entity.*;
import com.exam.exam_system.repository.ExamRepository;
import com.exam.exam_system.repository.ScoreRepository;
import com.exam.exam_system.repository.UserRepository;
import com.exam.exam_system.repository.AnswerRecordRepository;
import com.exam.exam_system.repository.HomeworkRepository;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    private final HomeworkRepository homeworkRepository;
    private final ExamRepository examRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;

    public ScoreService(HomeworkRepository homeworkRepository, ExamRepository examRepository,
                        AnswerRecordRepository answerRecordRepository, ScoreRepository scoreRepository,
                        UserRepository userRepository) {
        this.homeworkRepository = homeworkRepository;
        this.examRepository = examRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
    }

    // 添加成绩汇总缓存
    @Cacheable(value = "scoreSummaryCache", key = "#userId")
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

    // 添加考试成绩缓存
    @Cacheable(value = "examScoresCache", key = "#userId")
    public List<ExamScore> getExamScores(Long userId) {
        return scoreRepository.findByStudentId(userId).stream()
                .filter(s -> s.getExam() != null)
                .map(s -> {
                    ExamScore es = new ExamScore();
                    // 使用安全DTO替代实体类
                    es.setExam(new ExamDTO(s.getExam()));
                    es.setScore(s.getScore());
                    es.setTotalScore(s.getTotalScore());
                    es.setStatus(s.getStatus());
                    return es;
                })
                .collect(Collectors.toList());
    }

    // 添加作业成绩缓存
    @Cacheable(value = "homeworkScoresCache", key = "#userId")
    public List<HomeworkScore> getHomeworkScores(Long userId) {
        return scoreRepository.findByStudentId(userId).stream()
                .filter(s -> s.getHomework() != null)
                .map(s -> {
                    HomeworkScore hs = new HomeworkScore();
                    // 使用安全DTO替代实体类
                    hs.setHomework(new HomeworkDTO(s.getHomework()));
                    hs.setScore(s.getScore());
                    hs.setTotalScore(s.getTotalScore());
                    hs.setStatus(s.getStatus());
                    return hs;
                })
                .collect(Collectors.toList());
    }

    // 添加考试详情缓存
    @Cacheable(value = "examScoreDetailCache", key = "{#examId, #userId}")
    public ExamScoreDetail getExamScoreDetail(Long examId, Long userId) {
        Score score = scoreRepository.findByStudentAndExam(userId, examId)
                .orElseThrow(() -> new RuntimeException("成绩不存在"));

        List<AnswerRecord> answers = answerRecordRepository.findByStudentIdAndExamId(userId, examId);

        // 转换为安全DTO
        Exam exam = examRepository.findById(examId).orElse(null);
        ExamDTO examDTO = exam != null ? new ExamDTO(exam) : null;

        // 创建安全DTO对象
        ScoreDTO scoreDTO = new ScoreDTO(score);
        List<AnswerRecordDTO> answerDTOs = answers.stream()
                .map(AnswerRecordDTO::new)
                .collect(Collectors.toList());

        ExamScoreDetail detail = new ExamScoreDetail();
        detail.setScore(scoreDTO);
        detail.setAnswers(answerDTOs);
        detail.setExam(examDTO);

        return detail;
    }

    // 添加作业详情缓存
    @Cacheable(value = "homeworkScoreDetailCache", key = "{#homeworkId, #userId}")
    public HomeworkScoreDetail getHomeworkScoreDetail(Long homeworkId, Long userId) {
        Score score = scoreRepository.findByStudentAndHomework(userId, homeworkId)
                .orElseThrow(() -> new RuntimeException("成绩不存在"));

        List<AnswerRecord> answers = answerRecordRepository.findByStudentIdAndHomeworkId(userId, homeworkId);

        // 转换为安全DTO
        Homework homework = homeworkRepository.findById(homeworkId).orElse(null);
        HomeworkDTO homeworkDTO = homework != null ? new HomeworkDTO(homework) : null;

        // 创建安全DTO对象
        ScoreDTO scoreDTO = new ScoreDTO(score);
        List<AnswerRecordDTO> answerDTOs = answers.stream()
                .map(AnswerRecordDTO::new)
                .collect(Collectors.toList());

        HomeworkScoreDetail detail = new HomeworkScoreDetail();
        detail.setScore(scoreDTO);
        detail.setAnswers(answerDTOs);
        detail.setHomework(homeworkDTO);

        return detail;
    }
}

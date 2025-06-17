package com.exam.exam_system.service;

import com.exam.exam_system.dto.*;
import com.exam.exam_system.entity.*;
import com.exam.exam_system.repository.UserRepository;
import com.exam.exam_system.repository.AnswerRecordRepository;
import com.exam.exam_system.repository.ExamRepository;
import com.exam.exam_system.repository.QuestionRepository;
import com.exam.exam_system.repository.ScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ExamService {
    private static final Logger log = LoggerFactory.getLogger(ExamService.class);

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final ScoreRepository scoreRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final CacheManager cacheManager;

    public ExamService(UserRepository userRepository, ExamRepository examRepository, QuestionRepository questionRepository,
                       AnswerRecordRepository answerRecordRepository, ScoreRepository scoreRepository,
                       RedisTemplate<String, String> redisTemplate, CacheManager cacheManager){
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.scoreRepository = scoreRepository;
        this.redisTemplate = redisTemplate;
        this.cacheManager = cacheManager;
    }

    // 添加考试列表缓存
    @Cacheable(value = "examListCache", key = "{#userId, #root.methodName}")
    public List<ExamDTO> getExamList(Long userId){
        // 学生获取可参加的考试，教师获取自己创建的考试
        User currentUser = getCurrentUser();
        List<Exam> exams;

        if("TEACHER".equals(currentUser.getRole())){
            exams = examRepository.findByTeacherId(userId);
        } else {
            exams = examRepository.findOngoingExams(LocalDateTime.now());
        }

        // 转换为安全DTO
        return exams.stream()
                .map(ExamDTO::new)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "examDetailCache", key = "#examId")
    public ExamDetail getExamDetail(Long examId){
        Exam exam = examRepository.findById(examId)
                .orElseThrow(()->new RuntimeException("考试不存在"));

        // 转换为安全DTO
        ExamDTO examDTO = new ExamDTO(exam);
        List<Question> questions = questionRepository.findByExamId(examId);
        List<QuestionDTO> questionDTOs = questions.stream()
                .map(QuestionDTO::new)
                .collect(Collectors.toList());

        ExamDetail detail = new ExamDetail();
        detail.setExam(examDTO);
        detail.setQuestions(questionDTOs);
        detail.setSubject(examDTO.getSubject());
        detail.setTitle(examDTO.getTitle());
        detail.setStartTime(examDTO.getStartTime());
        detail.setEndTime(examDTO.getEndTime());
        detail.setDuration(examDTO.getDuration());

        // 检查学生是否已参加考试
        User currentUser = getCurrentUser();
        if ("STUDENT".equals(currentUser.getRole())) {
            Optional<Score> score = scoreRepository.findByStudentAndExam(currentUser.getId(), examId);
            detail.setHasTaken(score.isPresent());
        }

        return detail;
    }

    public ExamPaper startExam(Long examId, Long studentId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));

        // 检查考试是否在有效期内
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime())) {
            throw new RuntimeException("考试不在进行中");
        }

        // 检查是否已参加过考试
        if (scoreRepository.findByStudentAndExam(studentId, examId).isPresent()) {
            throw new RuntimeException("您已经参加过本次考试");
        }

        // 转换为安全DTO
        ExamDTO examDTO = new ExamDTO(exam);
        List<Question> questions = questionRepository.findByExamId(examId);
        List<QuestionDTO> questionDTOs = questions.stream()
                .map(QuestionDTO::new)
                .collect(Collectors.toList());

        ExamPaper paper = new ExamPaper();
        paper.setExam(examDTO);
        paper.setQuestions(questionDTOs);

        // 计算剩余时间
        long remainingSeconds = Duration.between(now, exam.getEndTime()).getSeconds();
        paper.setRemainingTime(remainingSeconds);

        return paper;
    }

    @Transactional
    public void submitExamAnswers(Long examId, Long studentId, List<Answer> answers) {
        // 幂等性检查
        String submitKey = "exam:submit:" + examId + ":" + studentId;
        Boolean canSubmit = redisTemplate.opsForValue().setIfAbsent(submitKey, "processing", 5, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(canSubmit)) {
            throw new RuntimeException("请勿重复提交考试");
        }

        try {
            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> new RuntimeException("考试不存在"));

            // 检查考试是否已结束
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(exam.getEndTime())) {
                throw new RuntimeException("考试已结束");
            }

            // 批量保存答案记录并自动批改客观题
            List<AnswerRecord> records = new ArrayList<>();
            List<Question> questions = questionRepository.findByExamId(examId);
            Map<Long, Question> questionMap = questions.stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));

            for (Answer answer : answers) {
                Question question = questionMap.get(answer.getQuestionId());
                if (question == null) continue;

                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("学生不存在"));

                Exam exam_ = examRepository.findById(examId)
                        .orElseThrow(() -> new RuntimeException("考试不存在"));

                AnswerRecord record = new AnswerRecord();
                record.setStudent(student);
                record.setQuestion(question);
                record.setExam(exam_);
                record.setAnswer(answer.getAnswer());

                // 自动批改客观题
                if (isAutoGradeQuestion(question.getType())) {
                    boolean isCorrect = question.getAnswer().equalsIgnoreCase(answer.getAnswer());
                    record.setIsCorrect(isCorrect);
                    record.setScore(isCorrect ? question.getScore() : 0);
                    record.setMastered(isCorrect);
                } else {
                    // 主观题需要教师批改
                    record.setIsCorrect(null);
                    record.setScore(null);
                }

                records.add(record);
            }

            // 批量保存答案
            answerRecordRepository.saveAll(records);
            // 异步计算成绩
            asyncCalculateExamScore(examId, studentId, records, exam.getTotalScore());
        } finally {
            redisTemplate.delete(submitKey);
        }
    }

    // 异步计算分数
    @Async
    @Transactional
    public void asyncCalculateExamScore(Long examId, Long studentId, List<AnswerRecord> records, int totalScore) {
        try {
            log.info("开始计算考试成绩，examId: {}, studentId: {}", examId, studentId);

            int calculatedScore = records.stream()
                    .filter(r -> r.getScore() != null)
                    .mapToInt(AnswerRecord::getScore)
                    .sum();

            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学生不存在"));

            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> new RuntimeException("考试不存在"));

            Score score = new Score();
            score.setStudent(student); // 使用完整实体
            score.setExam(exam); // 使用完整实体
            score.setScore(calculatedScore);
            score.setTotalScore(totalScore);
            score.setStatus(calculatedScore >= totalScore * 0.6 ? "PASSED" : "FAILED");
            scoreRepository.save(score);

            clearScoreCaches(studentId);

            log.info("考试成绩计算完成，examId: {}, studentId: {}, score: {}", examId, studentId, calculatedScore);
        } catch (Exception e) {
            // 记录日志并抛出自定义异常
            log.error("Error calculating exam score for examId: {}, studentId: {}", examId, studentId, e);
            throw new RuntimeException("成绩计算失败，请稍后重试");
        }
    }

    private void clearScoreCaches(Long studentId) {
        // 清除首页数据缓存
        Cache homeDataCache = cacheManager.getCache("homeDataCache");
        if (homeDataCache != null) {
            homeDataCache.evict(studentId);
        }

        // 清除成绩汇总缓存
        Cache scoreSummaryCache = cacheManager.getCache("scoreSummaryCache");
        if (scoreSummaryCache != null) {
            scoreSummaryCache.evict(studentId);
        }

        // 清除考试成绩列表缓存
        Cache examScoresCache = cacheManager.getCache("examScoresCache");
        if (examScoresCache != null) {
            examScoresCache.evict(studentId);
        }

        // 清除考试详情成绩缓存
        // 注意：这里使用模式匹配清除该学生所有考试的成绩详情缓存
        Set<String> examDetailKeys = redisTemplate.keys("examScoreDetailCache::" + studentId + ":*");
        if (examDetailKeys != null && !examDetailKeys.isEmpty()) {
            redisTemplate.delete(examDetailKeys);
        }

        // 清除最近成绩缓存（首页使用）
        Cache latestScoresCache = cacheManager.getCache("latestScoresCache");
        if (latestScoresCache != null) {
            latestScoresCache.evict(studentId);
        }

        // 清除未读通知缓存
        Cache notificationsCache = cacheManager.getCache("unreadNotifications");
        if (notificationsCache != null) {
            notificationsCache.evict(studentId);
        }
    }

    public long getTimeRemaining(Long examId, Long studentId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));

        // 检查是否已提交
        Optional<Score> score = scoreRepository.findByStudentAndExam(studentId, examId);
        if (score.isPresent()) {
            return 0; // 已提交，剩余时间为0
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(exam.getEndTime())) {
            return 0;
        }

        return Duration.between(now, exam.getEndTime()).getSeconds();
    }

    private boolean isAutoGradeQuestion(String type) {
        return "SINGLE_CHOICE".equals(type) ||
                "MULTIPLE_CHOICE".equals(type) ||
                "TRUE_FALSE".equals(type) ||
                "SHORT_ANSWER".equals(type);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}

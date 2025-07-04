// 作业服务
package com.exam.exam_system.service;

import com.exam.exam_system.dto.*;
import com.exam.exam_system.entity.*;
import com.exam.exam_system.repository.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class HomeworkService {

    private final UserRepository userRepository;
    private final HomeworkRepository homeworkRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final ScoreRepository scoreRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final CacheManager cacheManager;

    public HomeworkService(UserRepository userRepository, HomeworkRepository homeworkRepository, QuestionRepository questionRepository,
                           AnswerRecordRepository answerRecordRepository, ScoreRepository scoreRepository,
                           RedisTemplate<String, String> redisTemplate, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.homeworkRepository = homeworkRepository;
        this.questionRepository = questionRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.scoreRepository = scoreRepository;
        this.redisTemplate = redisTemplate;
        this.cacheManager = cacheManager;
    }

    // 添加作业列表缓存
    @Cacheable(value = "homeworkListCache", key = "{#userId, #root.methodName}")
    public List<HomeworkDTO> getHomeworkList(Long userId) {
        // 学生获取可做的作业，教师获取自己创建的作业
        User currentUser = getCurrentUser();
        List<Homework> homeworks;

        if ("TEACHER".equals(currentUser.getRole())) {
            homeworks = homeworkRepository.findByTeacherId(currentUser.getId());
        } else {
            homeworks = homeworkRepository.findActiveHomeworks(LocalDateTime.now());
        }

        // 转换为安全DTO
        return homeworks.stream()
                .map(HomeworkDTO::new)
                .collect(Collectors.toList());
    }

    // 添加作业详情缓存
    @Cacheable(value = "homeworkDetailCache", key = "#homeworkId")
    public HomeworkDetail getHomeworkDetail(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        // 转换为安全DTO
        HomeworkDTO homeworkDTO = new HomeworkDTO(homework);
        List<Question> questions = questionRepository.findByHomeworkId(homeworkId);
        List<QuestionDTO> questionDTOs = questions.stream()
                .map(QuestionDTO::new)
                .collect(Collectors.toList());

        HomeworkDetail detail = new HomeworkDetail();
        detail.setHomework(homeworkDTO);
        detail.setQuestions(questionDTOs);
        detail.setSubject(homeworkDTO.getSubject());
        detail.setTitle(homeworkDTO.getTitle());
        detail.setDeadline(homeworkDTO.getDeadline());

        // 检查学生是否已提交作业
        User currentUser = getCurrentUser();
        if ("STUDENT".equals(currentUser.getRole())) {
            Optional<Score> score = scoreRepository.findByStudentAndHomework(currentUser.getId(), homeworkId);
            detail.setHasSubmitted(score.isPresent());
        }

        return detail;
    }

    public HomeworkPaper startHomework(Long homeworkId, Long studentId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        // 检查作业是否过期
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(homework.getDeadline())) {
            throw new RuntimeException("作业已截止");
        }

        // 检查是否已提交作业
        if (scoreRepository.findByStudentAndHomework(studentId, homeworkId).isPresent()) {
            throw new RuntimeException("您已经提交过本次作业");
        }

        // 转换为安全DTO
        HomeworkDTO homeworkDTO = new HomeworkDTO(homework);
        List<Question> questions = questionRepository.findByHomeworkId(homeworkId);

        // 获取已保存的草稿答案
        List<AnswerRecord> draftAnswers = answerRecordRepository.findDraftByStudentAndHomework(studentId, homeworkId);
        Map<Long, String> draftAnswerMap = draftAnswers.stream()
                .collect(Collectors.toMap(
                        ar -> ar.getQuestion().getId(),
                        AnswerRecord::getAnswer
                ));

        List<QuestionDTO> questionDTOs = questions.stream()
                .map(question -> {
                    QuestionDTO dto = new QuestionDTO(question);
                    // 如果存在草稿答案，设置到DTO中
                    if (draftAnswerMap.containsKey(question.getId())) {
                        dto.setDraftAnswer(draftAnswerMap.get(question.getId()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        HomeworkPaper paper = new HomeworkPaper();
        paper.setHomework(homeworkDTO);
        paper.setQuestions(questionDTOs);
        paper.setRemainingTime(getTimeRemaining(homeworkId, studentId));

        return paper;
    }

    @Transactional
    @CacheEvict(value = {"homeworkListCache","homeworkDetailCache", "homeDataCache"}, allEntries = true)
    public void submitHomeworkAnswers(Long homeworkId, Long studentId, List<Answer> answers) {
        String submitKey = "homework:submit:" + homeworkId + ":" + studentId;
        Boolean canSubmit = redisTemplate.opsForValue().setIfAbsent(submitKey, "processing", 5, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(canSubmit)) {
            throw new RuntimeException("请勿重复提交作业");
        }

        try {
            Homework homework = homeworkRepository.findById(homeworkId)
                    .orElseThrow(() -> new RuntimeException("作业不存在"));

            // 检查作业是否已截止
            LocalDateTime now = LocalDateTime.now();

            List<AnswerRecord> records = new ArrayList<>();
            // 保存答案并自动批改客观题
            List<Question> questions = questionRepository.findByHomeworkId(homeworkId);
            Map<Long, Question> questionMap = questions.stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));

            for (Answer answer : answers) {
                Question question = questionMap.get(answer.getQuestionId());
                if (question == null) continue;

                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("学生不存在"));

                Homework h = homeworkRepository.findById(homeworkId)
                        .orElseThrow(() -> new RuntimeException("作业不存在"));

                AnswerRecord record = new AnswerRecord();
                record.setStudent(student);
                record.setQuestion(question);
                record.setHomework(h);
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

            answerRecordRepository.saveAll(records);

            // 异步处理成绩
            asyncProcessHomeworkScore(homeworkId, studentId, records, homework.getTotalScore(), homework.getDeadline());
        }   finally {
            redisTemplate.delete(submitKey);
        }
    }

    // 异步处理作业成绩
    @Async
    @Transactional
    public void asyncProcessHomeworkScore(Long homeworkId, Long studentId, List<AnswerRecord> records, int totalScore, LocalDateTime deadline) {
        int calculatedScore = records.stream()
                .filter(r -> r.getScore() != null)
                .mapToInt(AnswerRecord::getScore)
                .sum();

        boolean hasSubjective = records.stream()
                .anyMatch(r -> r.getScore() == null);

        boolean isLate = LocalDateTime.now().isAfter(deadline);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        Homework h = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        Score score = new Score();
        score.setStudent(student);
        score.setHomework(h);
        score.setScore(calculatedScore);
        score.setTotalScore(totalScore);
        score.setStatus(isLate ? "LATE" : hasSubjective ? "SUBMITTED" : "COMPLETED");
        scoreRepository.save(score);

        // 清除相关缓存
        clearHomeworkCaches(studentId);
    }

    private void clearHomeworkCaches(Long studentId) {
        // 清除首页数据缓存
        Cache homeDataCache = cacheManager.getCache("homeDataCache");
        if (homeDataCache != null) {
            homeDataCache.evict(studentId);
        }

        // 清除作业列表缓存
        Cache homeworkListCache = cacheManager.getCache("homeworkListCache");
        if (homeworkListCache != null) {
            homeworkListCache.evict(studentId);
        }

        // 清除作业详情缓存
        Cache homeworkDetailCache = cacheManager.getCache("homeworkDetailCache");
        if (homeworkDetailCache != null) {
            homeworkDetailCache.evict(studentId);
        }

        // 清除作业成绩列表缓存
        Cache homeworkScoresCache = cacheManager.getCache("homeworkScoresCache");
        if (homeworkScoresCache != null) {
            homeworkScoresCache.evict(studentId);
        }

        // 清除作业详情成绩缓存
        // 使用模式匹配清除该学生所有作业的成绩详情缓存
        Set<String> homeworkDetailKeys = redisTemplate.keys("homeworkScoreDetailCache::" + studentId + ":*");
        if (homeworkDetailKeys != null && !homeworkDetailKeys.isEmpty()) {
            redisTemplate.delete(homeworkDetailKeys);
        }

        // 清除最近作业缓存（首页使用）
        Cache recentHomeworksCache = cacheManager.getCache("recentHomeworksCache");
        if (recentHomeworksCache != null) {
            recentHomeworksCache.evict(studentId);
        }
    }

    /**
     * 保存作业草稿
     *
     * @param homeworkId 作业 ID
     * @param studentId 学生 ID
     * @param answers 学生的答案列表
     */
    @Transactional
    public void saveHomeworkDraft(Long homeworkId, Long studentId, List<Answer> answers) {
        // 检查是否已提交
        if(scoreRepository.findByStudentAndHomework(studentId, homeworkId).isPresent()){
            throw new RuntimeException("作业已提交，不能保存草稿");
        }

        // 删除旧草稿
        answerRecordRepository.deleteDraftByStudentAndHomework(studentId, homeworkId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        Homework h = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        for(Answer answer : answers){
            Question q = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("问题不存在"));
            AnswerRecord record = new AnswerRecord();
            record.setStudent(student);
            record.setQuestion(q);
            record.setHomework(h);
            record.setAnswer(answer.getAnswer());
            record.setIsDraft(true);
            record.setIsCorrect(null);
            record.setScore(null);

            answerRecordRepository.save(record);
        }
    }

    public long getTimeRemaining(Long homeworkId, Long studentId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));

        // 检查是否已提交
        Optional<Score> score = scoreRepository.findByStudentAndExam(studentId, homeworkId);
        if (score.isPresent()) {
            return 0; // 已提交，剩余时间为0
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(homework.getDeadline())) {
            return 0;
        }

        return Duration.between(now, homework.getDeadline()).getSeconds();
    }

    private boolean isAutoGradeQuestion(String type) {
        return "single".equals(type) ||
                "multiple".equals(type) ||
                "judge".equals(type) ||
                "short".equals(type);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}

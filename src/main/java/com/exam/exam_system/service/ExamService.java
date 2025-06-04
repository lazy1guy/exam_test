package com.exam.exam_system.service;

import com.exam.exam_system.entity.*;
import com.exam.exam_system.dto.*;
import com.exam.exam_system.repository.UserRepository;
import com.exam.exam_system.repository.AnswerRecordRepository;
import com.exam.exam_system.repository.ExamRepository;
import com.exam.exam_system.repository.QuestionRepository;
import com.exam.exam_system.repository.ScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final ScoreRepository scoreRepository;

    public ExamService(UserRepository userRepository, ExamRepository examRepository, QuestionRepository questionRepository, AnswerRecordRepository answerRecordRepository, ScoreRepository scoreRepository){
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.scoreRepository = scoreRepository;
    }

    public List<Exam> getExamList(Long userId){
        //学生获取可参加的考试，教师获取自己创建的考试
        User currentUser = getCurrentUser();
        if("TEACHER".equals(currentUser.getRole())){
            return examRepository.findByTeacherId(userId);
        }   else{
            return examRepository.findOngoingExams(LocalDateTime.now());
        }
    }

    public  ExamDetail getExamDetail(Long examId){
        Exam exam = examRepository.findById(examId)
                .orElseThrow(()->new RuntimeException("考试不存在"));

        ExamDetail detail = new ExamDetail();
        detail.setExam(exam);
        detail.setQuestions(questionRepository.findByExamId(examId));

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

        ExamPaper paper = new ExamPaper();
        paper.setExam(exam);
        paper.setQuestions(questionRepository.findByExamId(examId));

        // 计算剩余时间
        long remainingSeconds = Duration.between(now, exam.getEndTime()).getSeconds();
        paper.setRemainingTime(remainingSeconds);

        return paper;
    }

    @Transactional
    public void submitExamAnswers(Long examId, Long studentId, List<Answer> answers) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));

        // 检查考试是否已结束
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(exam.getEndTime())) {
            throw new RuntimeException("考试已结束");
        }

        // 保存答案并自动批改客观题
        List<Question> questions = questionRepository.findByExamId(examId);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        int totalScore = 0;
        int maxScore = exam.getTotalScore();

        for (Answer answer : answers) {
            Question question = questionMap.get(answer.getQuestionId());
            if (question == null) continue;

            AnswerRecord record = new AnswerRecord();
            record.setStudent(new User(studentId));
            record.setQuestion(question);
            record.setExam(new Exam(examId));
            record.setAnswer(answer.getAnswer());

            // 自动批改客观题
            if (isAutoGradeQuestion(question.getType())) {
                boolean isCorrect = question.getAnswer().equalsIgnoreCase(answer.getAnswer());
                record.setIsCorrect(isCorrect);
                record.setScore(isCorrect ? question.getScore() : 0);
                totalScore += record.getScore();
            } else {
                // 主观题需要教师批改
                record.setIsCorrect(null);
                record.setScore(null);
            }

            answerRecordRepository.save(record);
        }

        // 保存成绩
        Score score = new Score();
        score.setStudent(new User(studentId));
        score.setExam(new Exam(examId));
        score.setScore(totalScore);
        score.setTotalScore(maxScore);
        score.setStatus(totalScore >= maxScore * 0.6 ? "PASSED" : "FAILED");
        scoreRepository.save(score);
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
                "TRUE_FALSE".equals(type);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}

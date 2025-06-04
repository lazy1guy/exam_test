// 教师服务
package com.exam.exam_system.service;

import com.exam.exam_system.dto.*;
import com.exam.exam_system.entity.*;
import com.exam.exam_system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final ExamRepository examRepository;
    private final HomeworkRepository homeworkRepository;
    private final QuestionRepository questionRepository;
    private final ScoreRepository scoreRepository;
    private final AnswerRecordRepository answerRecordRepository;

    public TeacherService(ExamRepository examRepository, HomeworkRepository homeworkRepository,
                          QuestionRepository questionRepository, ScoreRepository scoreRepository,
                          AnswerRecordRepository answerRecordRepository) {
        this.examRepository = examRepository;
        this.homeworkRepository = homeworkRepository;
        this.questionRepository = questionRepository;
        this.scoreRepository = scoreRepository;
        this.answerRecordRepository = answerRecordRepository;
    }

    @Transactional
    public Exam createExam(ExamCreateRequest request) {
        Exam exam = new Exam();
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setSubject(request.getSubject());
        exam.setTeacher(new User(request.getTeacherId()));
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setDuration(request.getDuration());

        // 计算总分
        int totalScore = request.getQuestions().stream()
                .mapToInt(Question::getScore)
                .sum();
        exam.setTotalScore(totalScore);

        Exam savedExam = examRepository.save(exam);

        // 保存题目
        for (Question q : request.getQuestions()) {
            q.setExam(savedExam);
            questionRepository.save(q);
        }

        return savedExam;
    }

    @Transactional
    public Homework createHomework(HomeworkCreateRequest request) {
        Homework homework = new Homework();
        homework.setTitle(request.getTitle());
        homework.setDescription(request.getDescription());
        homework.setSubject(request.getSubject());
        homework.setTeacher(new User(request.getTeacherId()));
        homework.setDeadline(request.getDeadline());

        // 计算总分
        int totalScore = request.getQuestions().stream()
                .mapToInt(Question::getScore)
                .sum();
        homework.setTotalScore(totalScore);

        Homework savedHomework = homeworkRepository.save(homework);

        // 保存题目
        for (Question q : request.getQuestions()) {
            q.setHomework(savedHomework);
            questionRepository.save(q);
        }

        return savedHomework;
    }

    public ExamResults getExamResults(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));

        List<Score> scores = scoreRepository.findByExamId(examId);

        ExamResults results = new ExamResults();
        results.setExam(exam);

        // 计算平均分
        double avgScore = scores.stream()
                .mapToInt(Score::getScore)
                .average()
                .orElse(0.0);
        results.setAvgScore(avgScore);

        // 计算及格率
        long passedCount = scores.stream()
                .filter(s -> "PASSED".equals(s.getStatus()))
                .count();
        double passRate = scores.isEmpty() ? 0.0 : (double) passedCount / scores.size();
        results.setPassRate(passRate);

        // 学生成绩列表
        List<StudentScore> studentScores = scores.stream()
                .map(s -> {
                    StudentScore ss = new StudentScore();
                    ss.setStudentId(s.getStudent().getId());
                    ss.setStudentName(s.getStudent().getFullName());
                    ss.setScore(s.getScore());
                    ss.setTotalScore(s.getTotalScore());
                    return ss;
                })
                .collect(Collectors.toList());
        results.setStudentScores(studentScores);

        return results;
    }

    public HomeworkResults getHomeworkResults(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        List<Score> scores = scoreRepository.findByHomeworkId(homeworkId);

        HomeworkResults results = new HomeworkResults();
        results.setHomework(homework);

        // 计算平均分
        double avgScore = scores.stream()
                .mapToInt(Score::getScore)
                .average()
                .orElse(0.0);
        results.setAvgScore(avgScore);

        // 计算完成率
        long completedCount = scores.stream()
                .filter(s -> !"LATE".equals(s.getStatus()))
                .count();
        double completionRate = scores.isEmpty() ? 0.0 : (double) completedCount / scores.size();
        results.setCompletionRate(completionRate);

        // 学生成绩列表
        List<StudentScore> studentScores = scores.stream()
                .map(s -> {
                    StudentScore ss = new StudentScore();
                    ss.setStudentId(s.getStudent().getId());
                    ss.setStudentName(s.getStudent().getFullName());
                    ss.setScore(s.getScore());
                    ss.setTotalScore(s.getTotalScore());
                    ss.setStatus(s.getStatus());
                    return ss;
                })
                .collect(Collectors.toList());
        results.setStudentScores(studentScores);

        return results;
    }

    @Transactional
    public void gradeHomework(Long homeworkId, Long studentId, List<SubjectiveGrading> gradings) {
        for (SubjectiveGrading grading : gradings) {
            // 更新答题记录
            List<AnswerRecord> records = answerRecordRepository.findByStudentIdAndQuestionId(studentId, grading.getQuestionId());
            if (!records.isEmpty()) {
                AnswerRecord record = records.get(0); // 假设每个学生对每道题只有一个记录
                record.setScore(grading.getScore());
                record.setIsCorrect(grading.getScore() > 0); // 假设 setCorrect 方法存在
                answerRecordRepository.save(record);
            }
        }

        // 重新计算总分
        List<AnswerRecord> records = answerRecordRepository.findByStudentIdAndHomeworkId(studentId, homeworkId);
        int totalScore = records.stream()
                .filter(r -> r.getScore() != null)
                .mapToInt(AnswerRecord::getScore)
                .sum();

        // 更新成绩
        scoreRepository.findByStudentAndHomework(studentId, homeworkId)
                .ifPresent(score -> {
                    score.setScore(totalScore);
                    score.setStatus("COMPLETED");
                    scoreRepository.save(score);
                });
    }

    public void publishExamResults(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        exam.setPublished(true);
        examRepository.save(exam);
    }

}

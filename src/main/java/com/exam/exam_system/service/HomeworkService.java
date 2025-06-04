// 作业服务
package com.exam.exam_system.service;

import com.exam.exam_system.entity.*;
import com.exam.exam_system.dto.*;
import com.exam.exam_system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HomeworkService {

    private final UserRepository userRepository;
    private final HomeworkRepository homeworkRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final ScoreRepository scoreRepository;

    public HomeworkService(UserRepository userRepository, HomeworkRepository homeworkRepository, QuestionRepository questionRepository,
                           AnswerRecordRepository answerRecordRepository, ScoreRepository scoreRepository) {
        this.userRepository = userRepository;
        this.homeworkRepository = homeworkRepository;
        this.questionRepository = questionRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.scoreRepository = scoreRepository;
    }

    public List<Homework> getHomeworkList(Long userId) {
        // 学生获取可做的作业，教师获取自己创建的作业
        User currentUser = getCurrentUser();
        if ("TEACHER".equals(currentUser.getRole())) {
            return homeworkRepository.findByTeacherId(currentUser.getId());
        } else {
            return homeworkRepository.findActiveHomeworks(LocalDateTime.now());
        }
    }

    public HomeworkDetail getHomeworkDetail(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        HomeworkDetail detail = new HomeworkDetail();
        detail.setHomework(homework);
        detail.setQuestions(questionRepository.findByHomeworkId(homeworkId));

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

        HomeworkPaper paper = new HomeworkPaper();
        paper.setHomework(homework);
        paper.setQuestions(questionRepository.findByHomeworkId(homeworkId));

        return paper;
    }

    @Transactional
    public void submitHomeworkAnswers(Long homeworkId, Long studentId, List<Answer> answers) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        // 检查作业是否已截止
        LocalDateTime now = LocalDateTime.now();
        boolean isLate = now.isAfter(homework.getDeadline());

        // 保存答案并自动批改客观题
        List<Question> questions = questionRepository.findByHomeworkId(homeworkId);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        int totalScore = 0;
        int maxScore = homework.getTotalScore();
        boolean hasSubjective = false;

        for (Answer answer : answers) {
            Question question = questionMap.get(answer.getQuestionId());
            if (question == null) continue;

            AnswerRecord record = new AnswerRecord();
            record.setStudent(new User(studentId));
            record.setQuestion(question);
            record.setHomework(new Homework(homeworkId));
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
                hasSubjective = true;
            }

            answerRecordRepository.save(record);
        }

        // 保存成绩
        Score score = new Score();
        score.setStudent(new User(studentId));
        score.setHomework(new Homework(homeworkId));
        score.setScore(totalScore);
        score.setTotalScore(maxScore);
        score.setStatus(isLate ? "LATE" : hasSubjective ? "SUBMITTED" : "COMPLETED");
        scoreRepository.save(score);
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
        answerRecordRepository.deleteDraftBySyudentAndHomework(studentId, homeworkId);

        for(Answer answer : answers){
            AnswerRecord record = new AnswerRecord();
            record.setStudent(new User(studentId));
            record.setQuestion(new Question(answer.getQuestionId()));
            record.setHomework(new Homework(homeworkId));
            record.setAnswer(answer.getAnswer());
            record.setIsDraft(true);
            record.setIsCorrect(null);
            record.setScore(null);

            answerRecordRepository.save(record);
        }
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

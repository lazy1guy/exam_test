// 错题本服务
package com.exam.exam_test.service;

import com.exam.exam_test.entity.*;
import com.exam.exam_test.dto.*;
import com.exam.exam_test.repository.UserRepository;
import com.exam.exam_test.repository.AnswerRecordRepository;
import com.exam.exam_test.repository.QuestionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ErrorBookService {

    private final AnswerRecordRepository answerRecordRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public ErrorBookService(AnswerRecordRepository answerRecordRepository,
                            QuestionRepository questionRepository,
                            UserRepository userRepository) {
        this.answerRecordRepository = answerRecordRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    /**
     * 获取用户的错题本
     *
     * @param userId 用户ID
     * @return 错题列表
     */
    @Transactional(readOnly = true)
    public List<ErrorQuestion> getErrorBook(Long userId) {
        // 验证用户存在
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return answerRecordRepository.findByStudentIdAndIsCorrectFalse(userId).stream()
                .map(this::toErrorQuestion)
                .collect(Collectors.toList());
    }

    /**
     * 按科目获取用户的错题
     *
     * @param userId 用户ID
     * @param subject 科目
     * @return 错题列表
     */
    @Transactional(readOnly = true)
    public List<ErrorQuestion> getErrorBookBySubject(Long userId, String subject) {
        // 验证用户存在
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return answerRecordRepository.findByStudentIdAndQuestionSubject(userId, subject).stream()
                .filter(record -> !record.getIsCorrect())
                .map(this::toErrorQuestion)
                .collect(Collectors.toList());
    }

    /**
     * 从错题本移除题目（标记为已掌握）
     *
     * @param questionId 题目ID
     * @param userId 用户ID
     */
    @Transactional
    public void removeFromErrorBook(Long questionId, Long userId) {
        // 找到该用户关于这道题的所有错题记录
        List<AnswerRecord> records = answerRecordRepository.findByStudentIdAndQuestionId(userId, questionId);

        // 标记为已掌握（删除记录或更新状态）
        for (AnswerRecord record : records) {
            record.setMastered(true);
        }

        answerRecordRepository.saveAll(records);
    }

    /**
     * 添加错题笔记
     *
     * @param questionId 题目ID
     * @param userId 用户ID
     * @param note 笔记内容
     */
    @Transactional
    public void addErrorNote(Long questionId, Long userId, String note) {
        // 找到该用户关于这道题的所有错题记录
        List<AnswerRecord> records = answerRecordRepository.findByStudentIdAndQuestionId(userId, questionId);

        // 更新笔记（只更新错题记录）
        for (AnswerRecord record : records) {
            if (!record.getIsCorrect()) {
                record.setNote(note);
            }
        }

        answerRecordRepository.saveAll(records);
    }

    /**
     * 生成错题练习试卷
     *
     * @param userId 用户ID
     * @param count 题目数量
     * @return 练习试卷
     */
    @Transactional(readOnly = true)
    public PracticePaper generatePracticePaper(Long userId, int count) {
        // 验证用户存在
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取所有未掌握的错题
        List<AnswerRecord> errors = answerRecordRepository.findByStudentIdAndIsCorrectFalseAndMasteredFalse(userId);

        // 按科目分组
        Map<String, List<AnswerRecord>> errorsBySubject = errors.stream()
                .collect(Collectors.groupingBy(
                        ar -> ar.getQuestion().getSubject()
                ));

        // 为每个科目随机选择题目
        List<Question> practiceQuestions = new ArrayList<>();
        int subjectCount = errorsBySubject.size();

        if (subjectCount == 0) {
            // 如果没有错题，返回空试卷
            return new PracticePaper();
        }

        int perSubjectCount = Math.max(1, count / subjectCount);

        for (Map.Entry<String, List<AnswerRecord>> entry : errorsBySubject.entrySet()) {
            String subject = entry.getKey();
            List<Long> questionIds = entry.getValue().stream()
                    .map(ar -> ar.getQuestion().getId())
                    .distinct()
                    .collect(Collectors.toList());

            // 随机选择题目
            Collections.shuffle(questionIds);
            List<Long> selectedIds = questionIds.stream()
                    .limit(perSubjectCount)
                    .collect(Collectors.toList());

            if (!selectedIds.isEmpty()) {
                practiceQuestions.addAll(questionRepository.findAllById(selectedIds));
            }
        }

        // 如果题目不足，随机补充
        if (practiceQuestions.size() < count) {
            int needed = count - practiceQuestions.size();
            practiceQuestions.addAll(getRandomQuestions(needed));
        }

        // 如果题目过多，截取所需数量
        if (practiceQuestions.size() > count) {
            practiceQuestions = practiceQuestions.subList(0, count);
        }

        PracticePaper paper = new PracticePaper();
        paper.setQuestions(practiceQuestions);
        return paper;
    }

    /**
     * 将答题记录转换为错题对象
     */
    private ErrorQuestion toErrorQuestion(AnswerRecord record) {
        ErrorQuestion eq = new ErrorQuestion();
        eq.setId(record.getId());
        eq.setQuestion(record.getQuestion());
        eq.setUserAnswer(record.getAnswer());
        eq.setCorrectAnswer(record.getQuestion().getAnswer());
        eq.setSource(record.getExam() != null ? "考试" : record.getHomework() != null ? "作业" : "其他");
        eq.setErrorTime(record.getCreatedAt());
        eq.setNote(record.getNote());
        eq.setMastered(record.isMastered());
        return eq;
    }

    /**
     * 获取随机题目
     *
     * @param count 需要获取的数量
     * @return 随机题目列表
     */
    private List<Question> getRandomQuestions(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }

        // 使用分页随机获取题目
        long totalQuestions = questionRepository.count();
        if (totalQuestions == 0) {
            return Collections.emptyList();
        }

        // 随机选择一页
        int randomPage = new Random().nextInt((int) Math.ceil((double) totalQuestions / count));
        Pageable pageable = PageRequest.of(randomPage, count);

        return questionRepository.findAll(pageable).getContent();
    }
}

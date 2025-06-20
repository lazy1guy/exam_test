package com.exam.exam_system.service;

import com.exam.exam_system.dto.UserDTO;
import com.exam.exam_system.entity.User;
import com.exam.exam_system.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final HomeworkRepository homeworkRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final QuestionRepository questionRepository;
    private final ScoreRepository scoreRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, ExamRepository examRepository, HomeworkRepository homeworkRepository,
                        AnswerRecordRepository answerRecordRepository, QuestionRepository questionRepository, ScoreRepository scoreRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.homeworkRepository = homeworkRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.questionRepository = questionRepository;
        this.scoreRepository = scoreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 获取所有用户、外加提供按角色搜索
    public Page<User> getAllUsers(String search, String role, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                predicates.add(cb.or(
                        cb.like(root.get("username"), "%" + search + "%"),
                        cb.like(root.get("email"), "%" + search + "%")
                ));
            }

            if (role != null && !role.isEmpty()) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable);
    }

    // 根据 ID 获取用户
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 创建新用户
    @Transactional
    public UserDTO createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        UserDTO u = new UserDTO(savedUser);
        return u;
    }

    // 更新用户信息
    @Transactional
    public void updateUser(Long id, String username, String email, String role) {
        userRepository.updateUser(id, username, email, role);
    }

    // 删除用户
    @Transactional
    public void deleteUser(Long id) {
        // 获取用户信息
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOptional.get();
        String role = user.getRole();

        if ("TEACHER".equals(role)) {
            // 1. 先删除该教师创建的所有考试成绩
            scoreRepository.deleteByExamTeacherId(id);

            // 2. 删除该教师创建的所有作业成绩
            scoreRepository.deleteByHomeworkTeacherId(id);

            // 3. 删除与教师相关的所有答题记录
            answerRecordRepository.deleteByExamTeacherId(id);
            answerRecordRepository.deleteByHomeworkTeacherId(id);

            // 4. 删除教师创建的所有题目
            questionRepository.deleteByExamTeacherId(id);
            questionRepository.deleteByHomeworkTeacherId(id);

            // 5. 删除教师创建的所有考试
            examRepository.deleteByTeacherId(id);

            // 6. 删除教师创建的所有作业
            homeworkRepository.deleteByTeacherId(id);
        } else if ("STUDENT".equals(role)) {
            // 1. 先删除答题记录
            answerRecordRepository.deleteByStudentId(id);
            // 2. 再删除相关成绩
            scoreRepository.deleteByStudentId(id);
        }

        // 最后删除用户
        userRepository.deleteById(id);
    }
}



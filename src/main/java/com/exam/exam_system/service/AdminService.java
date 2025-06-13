package com.exam.exam_system.service;

import com.exam.exam_system.entity.User;
import com.exam.exam_system.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final HomeworkRepository homeworkRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final ScoreRepository scoreRepository;

    public AdminService(UserRepository userRepository, ExamRepository examRepository, HomeworkRepository homeworkRepository,
                        AnswerRecordRepository answerRecordRepository, ScoreRepository scoreRepository) {
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.homeworkRepository = homeworkRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.scoreRepository = scoreRepository;
    }

    // 获取所有用户
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // 根据 ID 获取用户
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 创建新用户
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
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

        // 根据角色执行不同操作
        String role = user.getRole();
        if ("TEACHER".equals(role)) {
            // 教师：将与该教师关联的所有考试记录的 teacher_id 置为空
            examRepository.nullifyTeacherIdByUserId(id);
            homeworkRepository.nullifyTeacherIdByUserId(id);
        } else if ("STUDENT".equals(role)) {
            // 学生：删除与其关联的所有答题记录和成绩记录
            answerRecordRepository.deleteByStudentId(id);
            scoreRepository.deleteByStudentId(id);
        }

        // 删除用户
        userRepository.deleteUserById(id);
    }
}



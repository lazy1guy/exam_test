package com.exam.exam_system.service;

import com.exam.exam_system.entity.User;
import com.exam.exam_system.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        userRepository.deleteUserById(id);
    }

}


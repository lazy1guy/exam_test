package com.exam.exam_system.config;

import com.exam.exam_system.entity.User;
import com.exam.exam_system.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public ApplicationRunner initializeAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 检查是否已存在管理员用户
            if (!userRepository.existsByUsername("admin")) {
                // 创建默认管理员用户
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123")); // 使用加密密码
                adminUser.setRole("ADMIN");
                adminUser.setEmail("admin@example.com");
                adminUser.setFullName("系统管理员");
                adminUser.setPhone("1234567890");
                adminUser.setAvatarUrl("https://example.com/avatar.png");

                // 保存管理员用户到数据库
                userRepository.save(adminUser);

                System.out.println("默认管理员用户已创建：用户名=admin，密码=admin123");
            } else {
                System.out.println("管理员用户已存在，无需创建。");
            }
        };
    }
}
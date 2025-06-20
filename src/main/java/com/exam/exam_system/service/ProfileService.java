// 个人资料服务
package com.exam.exam_system.service;

import com.exam.exam_system.entity.*;
import com.exam.exam_system.dto.UserProfile;
import com.exam.exam_system.dto.PasswordChangeRequest;
import com.exam.exam_system.dto.ScoreSummary;
import com.exam.exam_system.dto.LearningStatistics;
import com.exam.exam_system.repository.UserRepository;
import com.exam.exam_system.config.FileUploadProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ProfileService {

    private final FileUploadProperties fileUploadProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ScoreService scoreService;

    public ProfileService(FileUploadProperties fileUploadProperties, UserRepository userRepository, PasswordEncoder passwordEncoder, ScoreService scoreService) {
        this.fileUploadProperties = fileUploadProperties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.scoreService = scoreService;

        // 创建上传目录
        try {
            // 获取上传目录
            String uploadDir = fileUploadProperties.getUploadDir();
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }

    public UserProfile getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return toUserProfile(user);
    }

    public UserProfile updateProfile(UserProfile profile) {
        User user = userRepository.findById(profile.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setFullName(profile.getFullName());
        user.setEmail(profile.getEmail());
        user.setPhone(profile.getPhone());

        User updated = userRepository.save(user);
        return toUserProfile(updated);
    }

    public void changePassword(PasswordChangeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public String uploadAvatar(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("请选择要上传的文件");
        }

        try {
            // 1. 获取基础上传目录并确保avatars子目录存在
            String baseUploadDir = fileUploadProperties.getUploadDir(); // 获取配置的根目录，如 "upload-dir/"
            String avatarsDir = baseUploadDir + "/avatars/"; // 完整的avatars子目录路径

            // 创建avatars子目录（如果不存在）
            Files.createDirectories(Paths.get(avatarsDir));

            // 2. 生成唯一文件名（保留原始文件扩展名）
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = UUID.randomUUID() + fileExtension;

            // 3. 保存文件到指定目录（完整路径：upload-dir/avatars/filename）
            Path filePath = Paths.get(avatarsDir + newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. 更新用户头像URL（与WebConfig映射保持一致）
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            String avatarUrl = "/avatars/" + newFilename; // 注意：这里使用WebConfig中配置的URL前缀
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("上传失败", e);
        }
    }

    public LearningStatistics getLearningStatistics(Long userId) {
        // 实现学习统计逻辑
        ScoreSummary scoreSummary = scoreService.getScoreSummary(userId);

        LearningStatistics statistics = new LearningStatistics();
        statistics.setTotalExams(scoreSummary.getTotalExams());
        statistics.setTotalHomeworks(scoreSummary.getTotalHomeworks());
        statistics.setAvgExamScore(scoreSummary.getAvgExamScore());
        statistics.setAvgHomeworkScore(scoreSummary.getAvgHomeworkScore());
        statistics.setErrorCount(scoreSummary.getErrorCount());

        return statistics;
    }

    private UserProfile toUserProfile(User user) {
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setFullName(user.getFullName());
        profile.setEmail(user.getEmail());
        profile.setPhone(user.getPhone());
        profile.setRole(user.getRole());
        profile.setAvatarUrl(user.getAvatar() != null ?
                user.getAvatar() : "/avatars/default_avatar.png");
        return profile;
    }
}

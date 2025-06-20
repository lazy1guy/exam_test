// 个人资料控制器
package com.exam.exam_system.controller;

import com.exam.exam_system.dto.LearningStatistics;
import com.exam.exam_system.dto.PasswordChangeRequest;
import com.exam.exam_system.dto.UserProfile;
import com.exam.exam_system.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<UserProfile> getProfile(@RequestParam Long userId) {
        UserProfile profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserProfile> updateProfile(@RequestBody UserProfile profile) {
        UserProfile updated = profileService.updateProfile(profile);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeRequest request) {
        profileService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long userId) throws IOException {

        // 调用Service层方法，会自动处理文件保存和用户记录更新
        String avatarUrl = profileService.uploadAvatar(userId, file);
        return ResponseEntity.ok(avatarUrl);
    }

    @GetMapping("/statistics")
    public ResponseEntity<LearningStatistics> getLearningStatistics(@RequestParam Long userId) {
        LearningStatistics statistics = profileService.getLearningStatistics(userId);
        return ResponseEntity.ok(statistics);
    }
}

// 个人资料控制器
package com.exam.exam_test.controller;

import com.exam.exam_test.entity.*;
import com.exam.exam_test.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<String> uploadAvatar(@RequestParam Long userId,
                                               @RequestParam("file") MultipartFile file) {
        String avatarUrl = profileService.uploadAvatar(userId, file);
        return ResponseEntity.ok(avatarUrl);
    }

    @GetMapping("/statistics")
    public ResponseEntity<LearningStatistics> getLearningStatistics(@RequestParam Long userId) {
        LearningStatistics statistics = profileService.getLearningStatistics(userId);
        return ResponseEntity.ok(statistics);
    }
}

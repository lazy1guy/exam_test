package com.exam.exam_system.dto;

import lombok.Data;

@Data
public class UserProfile {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String avatarUrl;
}

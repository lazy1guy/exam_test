package com.exam.exam_test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String username;
    private String role;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}

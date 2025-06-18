package com.exam.exam_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Integer code;
    private Data data;

    @lombok.Data
    @AllArgsConstructor
    public static class Data {
        private Long userId;
        private String username;
        private String role;
        private String avatar;
        private String token;
        private String refreshToken;
        private Long expiresIn;
    }

    public AuthResponse(Integer code, Long userId, String token, String username, String role, String avatar) {
        this.code = code;
        this.data = new Data(userId, username, role, avatar, token, null, null);
    }


    public AuthResponse(Integer code, Long userId, String username, String role, String avatar, String token, String refreshToken, Long expiresIn) {
        this.code = code;
        this.data = new Data(userId, username, role, avatar, token, refreshToken, expiresIn);
    }
}

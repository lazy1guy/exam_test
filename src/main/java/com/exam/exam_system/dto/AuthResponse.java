package com.exam.exam_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Integer code;
    private Data data;
    public static class Data {
        private Long userId;
        private String username;
        private String role;
        private String token;
        private String refreshToken;
        private long expiresIn;

        public Data(Long userId, String username, String role, String token, String refreshToken, Long expiresIn) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.token = token;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
        }
    }
}

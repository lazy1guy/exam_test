package com.exam.exam_system.dto;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}

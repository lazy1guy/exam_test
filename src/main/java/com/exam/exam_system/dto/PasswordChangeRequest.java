package com.exam.exam_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}

// 认证控制器
package com.exam.exam_system.controller;

import com.exam.exam_system.dto.UserDTO;
import com.exam.exam_system.entity.*;
import com.exam.exam_system.dto.AuthResponse;
import com.exam.exam_system.dto.LoginRequest;
import com.exam.exam_system.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody User user) {
        UserDTO registeredUser = authService.register(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<AuthResponse> getUserInfo(@RequestParam String token) {
        AuthResponse response = authService.getUserInfo(token);
        return ResponseEntity.ok(response);
    }
}
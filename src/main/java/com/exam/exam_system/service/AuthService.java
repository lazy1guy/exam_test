package com.exam.exam_system.service;

import com.exam.exam_system.dto.UserDTO;
import com.exam.exam_system.entity.User;
import com.exam.exam_system.dto.AuthResponse;
import com.exam.exam_system.dto.LoginRequest;
import com.exam.exam_system.util.JwtTokenProvider;
import com.exam.exam_system.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager,
                       NotificationService notificationService, TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.notificationService = notificationService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Transactional
    public UserDTO register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已注册");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        UserDTO userDTO = new UserDTO(savedUser);

        // 发送欢迎通知
        notificationService.sendWelcomeNotification(savedUser);

        return userDTO;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            String accessToken = tokenProvider.generateToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user);

            return new AuthResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    accessToken,
                    refreshToken,
                    tokenProvider.getExpirationDate(accessToken).getTime()
            );
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("用户名或密码错误");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 生成新的访问令牌
        String newAccessToken = tokenProvider.generateToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                newAccessToken,
                newRefreshToken,
                tokenProvider.getExpirationDate(newAccessToken).getTime()
        );
    }

    public void logout(Long userId, String accessToken) {
        // 将令牌加入黑名单
        tokenBlacklistService.blacklistToken(accessToken);
    }
}
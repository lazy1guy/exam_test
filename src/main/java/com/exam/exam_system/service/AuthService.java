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

import java.util.Optional;

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

            String token = tokenProvider.generateToken(user);

            return new AuthResponse(20000, token);
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
        String token = tokenProvider.generateToken(user);

        return new AuthResponse(20000, token);
    }

    public AuthResponse getUserInfo(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException("无效的令牌");
        }

        String username = tokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return new AuthResponse(
                20000,
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getAvatar(),
                "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif",
                null,
                tokenProvider.getExpirationDate(token).getTime()
        );
    }

    public void logout(String token) {
        // 将令牌加入黑名单
        tokenBlacklistService.blacklistToken(token);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(username + "用户不存在"));
        return new UserDTO(user);
    }
}
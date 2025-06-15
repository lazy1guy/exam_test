package com.exam.exam_system.service;

import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());

    public void blacklistToken(String token) {
        if (token != null && !token.isBlank()) {  // 忽略空 Token
            blacklistedTokens.add(token);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return token != null && !token.isBlank() && blacklistedTokens.contains(token);
    }
}
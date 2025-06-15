package com.exam.exam_system.service;

import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());

    public void blacklistToken(String token) {
        if (token != null && !token.isBlank()) {
            blacklistedTokens.add(token);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        // 关键：空Token不视为黑名单
        if (token == null || token.isBlank()) {
            return false;
        }
        return blacklistedTokens.contains(token);
    }
}
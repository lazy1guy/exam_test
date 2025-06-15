package com.exam.exam_system.config;

import org.springframework.lang.NonNull;
import com.exam.exam_system.service.CustomUserDetailsService;
import com.exam.exam_system.service.TokenBlacklistService;
import com.exam.exam_system.util.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/",
            "/api/homeworks/active",
            "/api/exams/upcoming",
            "/api/exams/past"
    );

    public JwtTokenFilter(JwtTokenProvider tokenProvider,
                          CustomUserDetailsService userDetailsService,
                          TokenBlacklistService tokenBlacklistService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 跳过公开路径（无需认证的接口）
        String path = request.getRequestURI();
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        if (token != null && tokenProvider.validateToken(token)) {
            // 检查令牌是否在黑名单中
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "令牌已失效");
                return;
            }

            String username = tokenProvider.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

package com.exam.exam_system.config;

import org.springframework.lang.NonNull;
import com.exam.exam_system.service.CustomUserDetailsService;
import com.exam.exam_system.service.TokenBlacklistService;
import com.exam.exam_system.util.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;
import org.springframework.util.AntPathMatcher;

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
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 使用 Ant 风格路径匹配
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/**",
            "/api/homeworks/active",
            "/api/exams/**",
            "/api/home/**"
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

        String path = request.getRequestURI();
        logger.info("处理请求路径: " + path);

        // 检查是否为公开路径
        if (isPublicPath(path)) {
            logger.info("✅ 跳过公开路径: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        // 没有 Token 时返回 401
        if (token == null || token.isBlank()) {
            logger.warn("⛔ 无Token访问需认证路径: " + path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少认证Token");
            return;
        }

        try {
            // 验证 Token 有效性
            if (!tokenProvider.validateToken(token)) {
                logger.warn("⛔ 令牌无效或已过期: " + token);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "令牌无效或已过期");
                return;
            }

            // 检查令牌是否在黑名单中
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                logger.warn("⛔ 令牌已失效: " + token);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "令牌已失效");
                return;
            }

            // 获取用户名并加载用户信息
            String username = tokenProvider.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 创建认证对象
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("✅ 认证成功 - 用户: " + username);
        } catch (UsernameNotFoundException e) {
            logger.error("❌ 用户不存在: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户不存在");
            return;
        } catch (Exception e) {
            logger.error("❌ 认证过程中发生错误", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
            return;
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    // 使用 Ant 风格路径匹配
    private boolean isPublicPath(String requestUri) {
        return PUBLIC_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

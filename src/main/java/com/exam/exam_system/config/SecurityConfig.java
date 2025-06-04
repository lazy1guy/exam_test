package com.exam.exam_system.config;

import com.exam.exam_system.service.CustomUserDetailsService;
import com.exam.exam_system.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // 使用新的注解
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtTokenFilter jwtTokenFilter) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 公开访问的路径
                        .requestMatchers("/api/auth/**").permitAll() // 认证相关接口
                        .requestMatchers("/api/homeworks/active").permitAll() // 公开的作业信息
                        .requestMatchers("/api/exams/upcoming").permitAll() // 即将到来的考试
                        .requestMatchers("/api/exams/past").permitAll() // 已结束的考试

                        // 学生和教师角色可以访问的路径
                        .requestMatchers("/api/homeworks/**").hasAnyRole("TEACHER", "STUDENT") // 作业相关接口
                        .requestMatchers("/api/exams/**").hasAnyRole("TEACHER", "STUDENT") // 考试相关接口
                        .requestMatchers("/api/answers/**").hasRole("STUDENT") // 学生提交的答案
                        .requestMatchers("/api/scores/**").hasAnyRole("TEACHER", "STUDENT") // 成绩相关接口
                        .requestMatchers("/api/error-book/**").hasRole("STUDENT") // 错题本相关接口
                        .requestMatchers("/api/profile/**").authenticated() // 个人资料相关接口

                        // 教师专属路径
                        .requestMatchers("/api/teacher/**").hasRole("TEACHER") // 教师管理功能

                        // 首页公开数据
                        .requestMatchers("/api/home/**").permitAll()

                        // 默认规则：所有其他请求需要认证
                        .anyRequest().authenticated()

                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
// 首页服务
package com.exam.exam_system.service;

import com.exam.exam_system.dto.*;
import com.exam.exam_system.entity.*;
import com.exam.exam_system.repository.ExamRepository;
import com.exam.exam_system.repository.HomeworkRepository;
import com.exam.exam_system.repository.NotificationRepository;
import com.exam.exam_system.repository.ScoreRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeService {
    private final ExamRepository examRepository;
    private final HomeworkRepository homeworkRepository;
    private final ScoreRepository scoreRepository;
    private final NotificationRepository notificationRepository;

    public HomeService(ExamRepository examRepository, HomeworkRepository homeworkRepository,
                       ScoreRepository scoreRepository, NotificationRepository notificationRepository) {
        this.examRepository = examRepository;
        this.homeworkRepository = homeworkRepository;
        this.scoreRepository = scoreRepository;
        this.notificationRepository = notificationRepository;
    }

    @Cacheable(value = "homeDataCache", key = "#userId", unless = "#result == null")
    public HomeData getHomeData(Long userId){
        HomeData homeData = new HomeData();

        // 获取最近的3个作业并转换为安全DTO
        List<Homework> activeHomeworks = homeworkRepository.findActiveHomeworks(LocalDateTime.now());
        List<HomeworkDTO> homeworkDTOs = activeHomeworks.parallelStream()
                .limit(3)
                .map(HomeworkDTO::new)
                .collect(Collectors.toList());
        homeData.setRecentHomeworks(homeworkDTOs);

        // 获取最近的3个考试并转换为安全DTO
        List<Exam> upcomingExams = examRepository.findOngoingExams(LocalDateTime.now());
        List<ExamDTO> examDTOs = upcomingExams.parallelStream()
                .limit(3)
                .map(ExamDTO::new)
                .collect(Collectors.toList());
        homeData.setRecentExams(examDTOs);

        // 获取最新3个成绩并转换为安全DTO
        List<Score> scores = scoreRepository.findByStudentId(userId);
        scores.sort(Comparator.comparing(Score::getCreatedAt).reversed());
        List<ScoreDTO> scoreDTOs = scores.parallelStream()
                .limit(3)
                .map(ScoreDTO::new)
                .collect(Collectors.toList());
        homeData.setLatestScores(scoreDTOs);

        return homeData;
    }

    @Cacheable(value = "unreadNotifications", key = "#userId")
    public List<NotificationDTO> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadStatusFalse(userId);
        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "unreadNotifications", key = "#notification.user.id")
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
}

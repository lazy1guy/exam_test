// 首页服务
package com.exam.exam_system.service;

import com.exam.exam_system.entity.*;
import com.exam.exam_system.dto.HomeData;
import com.exam.exam_system.repository.ExamRepository;
import com.exam.exam_system.repository.HomeworkRepository;
import com.exam.exam_system.repository.NotificationRepository;
import com.exam.exam_system.repository.ScoreRepository;
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

    public HomeData getHomeData(Long userId){
        HomeData homeData = new HomeData();

        //获取最近的3个作业
        List<Homework> activeHomeworks = homeworkRepository.findActiveHomeworks(LocalDateTime.now());

        homeData.setRecentHomeworks(activeHomeworks.stream()
                .limit(3)
                .collect(Collectors.toList()));

        // 获取最近的3个考试
        List<Exam> upcomingExams = examRepository.findOngoingExams(LocalDateTime.now());
        homeData.setRecentExams(upcomingExams.stream()
                .limit(3)
                .collect(Collectors.toList()));

        // 获取最新3个成绩
        List<Score> scores = scoreRepository.findByStudentId(userId);
        scores.sort(Comparator.comparing(Score::getCreatedAt).reversed());
        homeData.setLatestScores(scores.stream()
                .limit(3)
                .collect(Collectors.toList()));

        return homeData;
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadStatusFalse(userId);
    }

    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
}

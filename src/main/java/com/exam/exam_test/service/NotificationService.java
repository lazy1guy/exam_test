package com.exam.exam_test.service;

import com.exam.exam_test.entity.User;
import com.exam.exam_test.entity.Notification;
import com.exam.exam_test.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void sendWelcomeNotification(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("欢迎使用作业考试系统");
        notification.setContent("感谢您注册我们的系统，祝您学习愉快！");
        notificationRepository.save(notification);
    }
}

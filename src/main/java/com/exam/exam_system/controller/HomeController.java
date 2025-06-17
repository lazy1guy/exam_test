// 首页控制器
package com.exam.exam_system.controller;

import com.exam.exam_system.dto.NotificationDTO;
import com.exam.exam_system.entity.*;
import com.exam.exam_system.dto.HomeData;
import com.exam.exam_system.service.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<HomeData> getHomeData(@RequestParam Long userId) {
        HomeData homeData = homeService.getHomeData(userId);
        return ResponseEntity.ok(homeData);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@RequestParam Long userId) {
        List<NotificationDTO> notifications = homeService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        homeService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}

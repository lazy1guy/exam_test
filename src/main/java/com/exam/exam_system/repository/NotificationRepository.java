package com.exam.exam_system.repository;

import com.exam.exam_system.entity.Notification;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 添加分页支持
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.readStatus = false ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndReadStatusFalse(@Param("userId") Long userId, Pageable pageable);

    // 添加缓存提示
    @QueryHints(value = {
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "notificationsCache")
    })
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.readStatus = false")
    List<Notification> findByUserIdAndReadStatusFalse(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.readStatus = true WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);

    // 添加批量标记为已读
    @Modifying
    @Query("UPDATE Notification n SET n.readStatus = true WHERE n.id IN :ids")
    void markMultipleAsRead(@Param("ids") Set<Long> ids);
}

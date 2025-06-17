package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class NotificationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private UserDTO user;
    private String title;
    private String content;
    private boolean readStatus;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        if (notification.getUser() != null) {
            this.user = new UserDTO(notification.getUser());
        }
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.readStatus = notification.isReadStatus();
        this.createdAt = notification.getCreatedAt();
    }

    public NotificationDTO() {}
}

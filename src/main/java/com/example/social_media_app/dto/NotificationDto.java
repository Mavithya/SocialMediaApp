package com.example.social_media_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private String type;
    private String message;
    private String actorName;
    private String actorProfilePicture;
    private Long entityId;
    private String entityType;
    private LocalDateTime createdAt;
    private Boolean isRead;
}

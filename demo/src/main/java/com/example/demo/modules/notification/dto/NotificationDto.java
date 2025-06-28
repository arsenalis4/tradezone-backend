package com.example.demo.modules.notification.dto;

import com.example.demo.modules.notification.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class NotificationDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "NotificationCreateRequest", description = "알림 생성 요청 DTO")
    public static class CreateRequest {
        @NotBlank(message = "제목은 필수입니다")
        private String title;

        @NotBlank(message = "메시지는 필수입니다")
        private String message;

        @NotNull(message = "알림 타입은 필수입니다")
        private NotificationType type;

        @NotNull(message = "수신자 ID는 필수입니다")
        private Long recipientId;

        private Long senderId;
        private Long relatedEntityId;
        private String relatedEntityType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "NotificationResponse", description = "알림 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private String message;
        private NotificationType type;
        private RecipientInfo recipient;
        private SenderInfo sender;
        private Long relatedEntityId;
        private String relatedEntityType;
        private Boolean isRead;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "NotificationRecipientInfo", description = "알림 수신자 정보 DTO")
    public static class RecipientInfo {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "NotificationSenderInfo", description = "알림 발신자 정보 DTO")
    public static class SenderInfo {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "NotificationMarkAsReadRequest", description = "알림 읽음 처리 요청 DTO")
    public static class MarkAsReadRequest {
        @NotNull(message = "알림 ID는 필수입니다")
        private Long notificationId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "NotificationUnreadCountResponse", description = "읽지 않은 알림 수 응답 DTO")
    public static class UnreadCountResponse {
        private Long count;
    }
} 
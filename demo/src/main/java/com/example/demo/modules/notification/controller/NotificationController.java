package com.example.demo.modules.notification.controller;

import com.example.demo.modules.notification.dto.NotificationDto;
import com.example.demo.modules.notification.enums.NotificationType;
import com.example.demo.modules.notification.service.NotificationService;
import com.example.demo.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification", description = "알림 관리 API")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 생성", description = "새로운 알림을 생성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "알림 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<NotificationDto.Response> createNotification(
            @Valid @RequestBody NotificationDto.CreateRequest request) {
        try {
            NotificationDto.Response response = notificationService.createNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "내 알림 목록", description = "현재 로그인한 사용자의 모든 알림을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my")
    public ResponseEntity<List<NotificationDto.Response>> getMyNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<NotificationDto.Response> notifications = notificationService
                .getNotificationsByRecipient(userPrincipal.getId());
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "내 알림 목록 (페이징)", description = "현재 로그인한 사용자의 알림을 페이징으로 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my/paged")
    public ResponseEntity<Page<NotificationDto.Response>> getMyNotificationsPaged(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NotificationDto.Response> notifications = notificationService
                .getNotificationsByRecipient(userPrincipal.getId(), page, size);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "읽지 않은 알림", description = "현재 로그인한 사용자의 읽지 않은 알림을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my/unread")
    public ResponseEntity<List<NotificationDto.Response>> getUnreadNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<NotificationDto.Response> notifications = notificationService
                .getUnreadNotifications(userPrincipal.getId());
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "읽지 않은 알림 (페이징)", description = "현재 로그인한 사용자의 읽지 않은 알림을 페이징으로 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my/unread/paged")
    public ResponseEntity<Page<NotificationDto.Response>> getUnreadNotificationsPaged(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NotificationDto.Response> notifications = notificationService
                .getUnreadNotifications(userPrincipal.getId(), page, size);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "타입별 알림", description = "특정 타입의 알림을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my/type/{type}")
    public ResponseEntity<List<NotificationDto.Response>> getNotificationsByType(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable NotificationType type) {
        List<NotificationDto.Response> notifications = notificationService
                .getNotificationsByType(userPrincipal.getId(), type);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "읽지 않은 알림 수", description = "현재 로그인한 사용자의 읽지 않은 알림 개수를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my/unread/count")
    public ResponseEntity<NotificationDto.UnreadCountResponse> getUnreadCount(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        long count = notificationService.getUnreadCount(userPrincipal.getId());
        NotificationDto.UnreadCountResponse response = NotificationDto.UnreadCountResponse.builder()
                .count(count)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음으로 표시합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            notificationService.markAsRead(id, userPrincipal.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "모든 알림 읽음 처리", description = "현재 로그인한 사용자의 모든 알림을 읽음으로 표시합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/my/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.markAllAsRead(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            notificationService.deleteNotification(id, userPrincipal.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "읽은 알림 모두 삭제", description = "현재 로그인한 사용자의 읽은 알림을 모두 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/my/read")
    public ResponseEntity<Void> deleteReadNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.deleteReadNotifications(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }
} 
package com.example.demo.modules.notification.service;

import com.example.demo.modules.notification.dto.NotificationDto;
import com.example.demo.modules.notification.entity.Notification;
import com.example.demo.modules.notification.enums.NotificationType;
import com.example.demo.modules.notification.repository.NotificationRepository;
import com.example.demo.modules.user.entity.User;
import com.example.demo.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationDto.Response createNotification(NotificationDto.CreateRequest request) {
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다: " + request.getRecipientId()));

        User sender = null;
        if (request.getSenderId() != null) {
            sender = userRepository.findById(request.getSenderId())
                    .orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없습니다: " + request.getSenderId()));
        }

        Notification notification = Notification.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .recipient(recipient)
                .sender(sender)
                .relatedEntityId(request.getRelatedEntityId())
                .relatedEntityType(request.getRelatedEntityType())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("새 알림 생성됨: {} for {}", savedNotification.getTitle(), recipient.getEmail());

        return convertToResponse(savedNotification);
    }

    @Transactional(readOnly = true)
    public NotificationDto.Response getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다: " + id));

        return convertToResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto.Response> getNotificationsByRecipient(Long recipientId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto.Response> getNotificationsByRecipient(Long recipientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto.Response> getUnreadNotifications(Long recipientId) {
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto.Response> getUnreadNotifications(Long recipientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto.Response> getNotificationsByType(Long recipientId, NotificationType type) {
        return notificationRepository.findByRecipientIdAndTypeOrderByCreatedAtDesc(recipientId, type)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long recipientId) {
        return notificationRepository.countUnreadByRecipientId(recipientId);
    }

    public void markAsRead(Long notificationId, Long recipientId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다: " + notificationId));

        // 수신자 권한 확인
        if (!notification.getRecipient().getId().equals(recipientId)) {
            throw new RuntimeException("알림에 대한 권한이 없습니다");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
        log.info("알림 읽음 처리됨: {}", notificationId);
    }

    public void markAllAsRead(Long recipientId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId);

        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);

        log.info("모든 알림 읽음 처리됨 for user: {}", recipientId);
    }

    public void deleteNotification(Long notificationId, Long recipientId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다: " + notificationId));

        // 수신자 권한 확인
        if (!notification.getRecipient().getId().equals(recipientId)) {
            throw new RuntimeException("알림에 대한 권한이 없습니다");
        }

        notificationRepository.delete(notification);
        log.info("알림 삭제됨: {}", notificationId);
    }

    @Transactional
    public void deleteReadNotifications(Long recipientId) {
        notificationRepository.deleteByRecipientIdAndIsReadTrue(recipientId);
        log.info("읽은 알림 모두 삭제됨 for user: {}", recipientId);
    }

    // 편의 메소드들
    public void createPostLikeNotification(Long postId, Long postAuthorId, Long likerId) {
        if (postAuthorId.equals(likerId)) return; // 자신의 게시글에는 알림 안함

        User liker = userRepository.findById(likerId).orElse(null);
        if (liker == null) return;

        NotificationDto.CreateRequest request = NotificationDto.CreateRequest.builder()
                .title("게시글 좋아요")
                .message(liker.getName() + "님이 회원님의 게시글을 좋아합니다.")
                .type(NotificationType.POST_LIKE)
                .recipientId(postAuthorId)
                .senderId(likerId)
                .relatedEntityId(postId)
                .relatedEntityType("POST")
                .build();

        createNotification(request);
    }

    public void createCommentNotification(Long postId, Long postAuthorId, Long commenterId) {
        if (postAuthorId.equals(commenterId)) return;

        User commenter = userRepository.findById(commenterId).orElse(null);
        if (commenter == null) return;

        NotificationDto.CreateRequest request = NotificationDto.CreateRequest.builder()
                .title("새 댓글")
                .message(commenter.getName() + "님이 회원님의 게시글에 댓글을 남겼습니다.")
                .type(NotificationType.POST_COMMENT)
                .recipientId(postAuthorId)
                .senderId(commenterId)
                .relatedEntityId(postId)
                .relatedEntityType("POST")
                .build();

        createNotification(request);
    }

    private NotificationDto.Response convertToResponse(Notification notification) {
        NotificationDto.RecipientInfo recipientInfo = NotificationDto.RecipientInfo.builder()
                .id(notification.getRecipient().getId())
                .name(notification.getRecipient().getName())
                .email(notification.getRecipient().getEmail())
                .build();

        NotificationDto.SenderInfo senderInfo = null;
        if (notification.getSender() != null) {
            senderInfo = NotificationDto.SenderInfo.builder()
                    .id(notification.getSender().getId())
                    .name(notification.getSender().getName())
                    .email(notification.getSender().getEmail())
                    .build();
        }

        return NotificationDto.Response.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .recipient(recipientInfo)
                .sender(senderInfo)
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
} 
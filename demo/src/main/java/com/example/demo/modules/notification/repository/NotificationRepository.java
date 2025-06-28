package com.example.demo.modules.notification.repository;

import com.example.demo.modules.notification.entity.Notification;
import com.example.demo.modules.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

    Page<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    List<Notification> findByRecipientIdAndTypeOrderByCreatedAtDesc(Long recipientId, NotificationType type);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :recipientId AND n.isRead = false")
    long countUnreadByRecipientId(@Param("recipientId") Long recipientId);

    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND " +
           "(:type IS NULL OR n.type = :type) AND " +
           "(:isRead IS NULL OR n.isRead = :isRead) " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findNotificationsWithFilters(
            @Param("recipientId") Long recipientId,
            @Param("type") NotificationType type,
            @Param("isRead") Boolean isRead,
            Pageable pageable
    );

    void deleteByRecipientIdAndIsReadTrue(Long recipientId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :recipientId")
    long countByRecipientId(@Param("recipientId") Long recipientId);

    List<Notification> findBySenderIdOrderByCreatedAtDesc(Long senderId);
} 
package com.example.demo.modules.chat.repository;

import com.example.demo.modules.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdAndIsDeletedFalseOrderByCreatedAtAsc(Long chatRoomId);
    
    Page<ChatMessage> findByChatRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);
    
    List<ChatMessage> findBySenderIdAndIsDeletedFalseOrderByCreatedAtDesc(Long senderId);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.isDeleted = false " +
           "AND m.createdAt >= :since ORDER BY m.createdAt ASC")
    List<ChatMessage> findRecentMessages(@Param("roomId") Long roomId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.isDeleted = false")
    long countByChatRoomIdAndIsDeletedFalse(@Param("roomId") Long roomId);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.sender.id = :senderId AND m.isDeleted = false")
    long countBySenderIdAndIsDeletedFalse(@Param("senderId") Long senderId);
    
    // 통계용 메서드
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.isDeleted = false " +
           "ORDER BY m.createdAt DESC")
    List<ChatMessage> findTop50ByChatRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(@Param("roomId") Long roomId, Pageable pageable);
} 
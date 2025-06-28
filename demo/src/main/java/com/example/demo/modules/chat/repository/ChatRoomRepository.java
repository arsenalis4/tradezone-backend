package com.example.demo.modules.chat.repository;

import com.example.demo.modules.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByIsActiveTrueOrderByCreatedAtDesc();
    
    Page<ChatRoom> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    
    Optional<ChatRoom> findByIdAndIsActiveTrue(Long id);
    
    List<ChatRoom> findByCreatorIdAndIsActiveTrueOrderByCreatedAtDesc(Long creatorId);
    
    @Query("SELECT r FROM ChatRoom r WHERE r.isActive = true AND r.type = :type ORDER BY r.createdAt DESC")
    List<ChatRoom> findByTypeAndIsActiveTrue(@Param("type") ChatRoom.RoomType type);
    
    @Query("SELECT r FROM ChatRoom r WHERE r.isActive = true AND " +
           "(r.name LIKE %:keyword% OR r.description LIKE %:keyword%) " +
           "ORDER BY r.createdAt DESC")
    List<ChatRoom> findByKeywordAndIsActiveTrue(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(r) FROM ChatRoom r WHERE r.isActive = true")
    long countActiveRooms();
    
    @Query("SELECT COUNT(r) FROM ChatRoom r WHERE r.creator.id = :creatorId AND r.isActive = true")
    long countByCreatorIdAndIsActiveTrue(@Param("creatorId") Long creatorId);
} 
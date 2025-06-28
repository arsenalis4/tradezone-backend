package com.example.demo.modules.user.repository;

import com.example.demo.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();

    @Query("SELECT u FROM User u WHERE u.nickname LIKE %:nickname% AND u.isActive = true")
    List<User> findByNicknameContainingAndIsActiveTrue(@Param("nickname") String nickname);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    long countByIsActiveTrue();

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<User> findByIsActiveTrueOrderByCreatedAtDesc();
} 
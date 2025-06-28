package com.example.demo.modules.comment.repository;

import com.example.demo.modules.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdAndParentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);

    List<Comment> findByParentIdAndIsDeletedFalseOrderByCreatedAtAsc(Long parentId);

    Optional<Comment> findByIdAndIsDeletedFalse(Long id);

    List<Comment> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.isDeleted = false")
    long countByPostIdAndIsDeletedFalse(@Param("postId") Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.author.id = :authorId AND c.isDeleted = false")
    long countByAuthorIdAndIsDeletedFalse(@Param("authorId") Long authorId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.isDeleted = false " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findAllByPostIdAndIsDeletedFalse(@Param("postId") Long postId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
} 
package com.example.demo.modules.post.repository;

import com.example.demo.modules.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndIsPublishedTrue(Long id);

    Page<Post> findByIsPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByAuthorIdAndIsPublishedTrueOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    Page<Post> findByCategoryIdAndIsPublishedTrueOrderByCreatedAtDesc(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND " +
           "(p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findByKeywordAndIsPublishedTrue(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
           "(:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:authorId IS NULL OR p.author.id = :authorId) AND " +
           "(:isPublished IS NULL OR p.isPublished = :isPublished)")
    Page<Post> findPostsWithFilters(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("authorId") Long authorId,
            @Param("isPublished") Boolean isPublished,
            Pageable pageable
    );

    List<Post> findTop10ByIsPublishedTrueOrderByViewCountDesc();

    @Query("SELECT COUNT(p) FROM Post p WHERE p.isPublished = true")
    long countPublishedPosts();

    @Query("SELECT COUNT(p) FROM Post p WHERE p.author.id = :authorId AND p.isPublished = true")
    long countPostsByAuthor(@Param("authorId") Long authorId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.category.id = :categoryId AND p.isPublished = true")
    long countPostsByCategory(@Param("categoryId") Long categoryId);
    
    // 통계용 메서드
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
} 
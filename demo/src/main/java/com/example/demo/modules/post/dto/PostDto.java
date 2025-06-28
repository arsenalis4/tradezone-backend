package com.example.demo.modules.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class PostDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PostCreateRequest", description = "게시글 생성 요청 DTO")
    public static class CreateRequest {
        @NotBlank(message = "제목은 필수입니다")
        private String title;

        @NotBlank(message = "내용은 필수입니다")
        private String content;

        private Long categoryId;
        
        @Builder.Default
        private Boolean isPublished = true;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PostUpdateRequest", description = "게시글 수정 요청 DTO")
    public static class UpdateRequest {
        private String title;
        private String content;
        private Long categoryId;
        private Boolean isPublished;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PostResponse", description = "게시글 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private AuthorInfo author;
        private CategoryInfo category;
        private Integer viewCount;
        private Integer likeCount;
        private Boolean isPublished;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PostAuthorInfo", description = "게시글 작성자 정보 DTO")
    public static class AuthorInfo {
        private Long id;
        private String name;
        private String nickname;
        private String profileImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PostCategoryInfo", description = "게시글 카테고리 정보 DTO")
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PostSearchRequest", description = "게시글 검색 요청 DTO")
    public static class SearchRequest {
        private String keyword;
        private Long categoryId;
        private Long authorId;
        private Boolean isPublished;
        
        @Builder.Default
        private String sortBy = "createdAt";
        
        @Builder.Default
        private String sortDirection = "DESC";
        
        @Builder.Default
        private Integer page = 0;
        
        @Builder.Default
        private Integer size = 10;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PostLikeRequest", description = "게시글 좋아요 요청 DTO")
    public static class LikeRequest {
        @NotNull(message = "게시글 ID는 필수입니다")
        private Long postId;
    }
} 
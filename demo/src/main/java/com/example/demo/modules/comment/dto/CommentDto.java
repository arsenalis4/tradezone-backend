package com.example.demo.modules.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class CommentDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentCreateRequest", description = "댓글 생성 요청 DTO")
    public static class CreateRequest {
        @NotBlank(message = "댓글 내용은 필수입니다")
        private String content;

        @NotNull(message = "게시글 ID는 필수입니다")
        private Long postId;

        private Long parentId; // 대댓글인 경우
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentUpdateRequest", description = "댓글 수정 요청 DTO")
    public static class UpdateRequest {
        @NotBlank(message = "댓글 내용은 필수입니다")
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentResponse", description = "댓글 응답 DTO")
    public static class Response {
        private Long id;
        private String content;
        private AuthorInfo author;
        private PostInfo post;
        private Long parentId;
        private List<Response> replies;
        private Integer likeCount;
        private Boolean isDeleted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentAuthorInfo", description = "댓글 작성자 정보 DTO")
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
    @Schema(name = "CommentPostInfo", description = "댓글 게시글 정보 DTO")
    public static class PostInfo {
        private Long id;
        private String title;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentLikeRequest", description = "댓글 좋아요 요청 DTO")
    public static class LikeRequest {
        @NotNull(message = "댓글 ID는 필수입니다")
        private Long commentId;
    }
} 
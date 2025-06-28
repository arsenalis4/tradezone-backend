package com.example.demo.modules.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CategoryDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CategoryCreateRequest", description = "카테고리 생성 요청 DTO")
    public static class CreateRequest {
        @NotBlank(message = "카테고리 이름은 필수입니다")
        private String name;

        private String description;
        private String color;
        
        @Builder.Default
        private Boolean isActive = true;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CategoryUpdateRequest", description = "카테고리 수정 요청 DTO")
    public static class UpdateRequest {
        private String name;
        private String description;
        private String color;
        private Boolean isActive;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CategoryResponse", description = "카테고리 응답 DTO")
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private String color;
        private Boolean isActive;
        private Integer postCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
} 
package com.example.demo.modules.statistics.dto;

import com.example.demo.modules.statistics.enums.StatisticType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StatisticsDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "통계 타입은 필수입니다")
        private StatisticType type;

        @NotBlank(message = "통계 이름은 필수입니다")
        private String name;

        @NotNull(message = "통계 값은 필수입니다")
        private Long value;

        private LocalDate date;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private StatisticType type;
        private String name;
        private Long value;
        private LocalDate date;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardResponse {
        private Long totalUsers;
        private Long totalPosts;
        private Long totalComments;
        private Long totalFiles;
        private Long todayUsers;
        private Long todayPosts;
        private Long todayComments;
        private Long monthlySignups;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartData {
        private String label;
        private Long value;
        private LocalDate date;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopularCategoryResponse {
        private String categoryName;
        private Long postCount;
        private String categoryColor;
    }
} 
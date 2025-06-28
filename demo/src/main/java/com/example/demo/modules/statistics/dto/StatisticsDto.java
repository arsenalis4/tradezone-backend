package com.example.demo.modules.statistics.dto;

import com.example.demo.modules.statistics.enums.StatisticType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(name = "StatisticsCreateRequest", description = "통계 생성 요청 DTO")
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
    @Schema(name = "StatisticsResponse", description = "통계 응답 DTO")
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
    @Schema(name = "StatisticsDashboardResponse", description = "대시보드 통계 응답 DTO")
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
    @Schema(name = "StatisticsChartData", description = "통계 차트 데이터 DTO")
    public static class ChartData {
        private String label;
        private Long value;
        private LocalDate date;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "StatisticsPopularCategoryResponse", description = "인기 카테고리 통계 응답 DTO")
    public static class PopularCategoryResponse {
        private String categoryName;
        private Long postCount;
        private String categoryColor;
    }
} 
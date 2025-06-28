package com.example.demo.modules.statistics.controller;

import com.example.demo.modules.statistics.dto.StatisticsDto;
import com.example.demo.modules.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "통계 관리 API")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "대시보드 통계", description = "대시보드에서 사용할 주요 통계 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "통계 조회 성공")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<StatisticsDto.DashboardResponse> getDashboardStatistics() {
        StatisticsDto.DashboardResponse response = statisticsService.getDashboardStatistics();
        return ResponseEntity.ok(response);
    }
} 
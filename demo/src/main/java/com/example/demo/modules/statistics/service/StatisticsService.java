package com.example.demo.modules.statistics.service;

import com.example.demo.modules.statistics.dto.StatisticsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    public StatisticsDto.DashboardResponse getDashboardStatistics() {
        return StatisticsDto.DashboardResponse.builder()
                .totalUsers(0L)
                .totalPosts(0L)
                .totalComments(0L)
                .totalFiles(0L)
                .todayUsers(0L)
                .todayPosts(0L)
                .todayComments(0L)
                .monthlySignups(0L)
                .build();
    }
} 
package com.example.demo.modules.statistics.service;

import com.example.demo.modules.comment.repository.CommentRepository;
import com.example.demo.modules.file.repository.FileRepository;
import com.example.demo.modules.post.repository.PostRepository;
import com.example.demo.modules.statistics.dto.StatisticsDto;
import com.example.demo.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;

    public StatisticsDto.DashboardResponse getDashboardStatistics() {
        // 오늘 날짜 범위 설정
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        
        // 이번 달 범위 설정
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        try {
            // 총 통계
            long totalUsers = userRepository.countByIsActiveTrue();
            long totalPosts = postRepository.count();
            long totalComments = commentRepository.count();
            long totalFiles = fileRepository.count();

            // 오늘 통계
            long todayUsers = userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            long todayPosts = postRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            long todayComments = commentRepository.countByCreatedAtBetween(startOfDay, endOfDay);

            // 이번 달 가입자 수
            long monthlySignups = userRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);

            log.debug("Dashboard statistics - Total: Users={}, Posts={}, Comments={}, Files={}, " +
                     "Today: Users={}, Posts={}, Comments={}, Monthly Signups={}", 
                     totalUsers, totalPosts, totalComments, totalFiles,
                     todayUsers, todayPosts, todayComments, monthlySignups);

            return StatisticsDto.DashboardResponse.builder()
                    .totalUsers(totalUsers)
                    .totalPosts(totalPosts)
                    .totalComments(totalComments)
                    .totalFiles(totalFiles)
                    .todayUsers(todayUsers)
                    .todayPosts(todayPosts)
                    .todayComments(todayComments)
                    .monthlySignups(monthlySignups)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching dashboard statistics: {}", e.getMessage());
            // 오류 발생 시 기본값 반환
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
} 
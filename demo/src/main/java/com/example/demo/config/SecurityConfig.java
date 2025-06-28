package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationEntryPoint;
import com.example.demo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth
                    // 인증 관련 API
                    .requestMatchers("/api/auth/**").permitAll()
                    
                    // Swagger 및 정적 리소스
                    .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/test/**", "/uploads/**").permitAll()
                    
                    // 게시글 API - GET 요청은 공개, 나머지는 인증 필요
                    .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/posts/**/like").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/posts").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()
                    
                    // 카테고리 API - GET 요청은 공개, 나머지는 인증 필요
                    .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/categories").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/categories/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/categories/**").authenticated()
                    
                    // 댓글 API - 특정 GET 요청은 공개, 나머지는 인증 필요
                    .requestMatchers(HttpMethod.GET, "/api/comments/*").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/comments/post/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/comments/author/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/comments/count/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/comments/**/like").authenticated()
                    .requestMatchers("/api/comments/**").authenticated()
                    
                    // 파일 API - GET은 공개, POST는 인증 필요
                    .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/files/**").authenticated()
                    
                    // 알림 API - 모든 요청 인증 필요
                    .requestMatchers("/api/notifications/**").authenticated()
                    
                    // 통계 API - 모든 요청 인증 필요
                    .requestMatchers("/api/statistics/**").authenticated()
                    
                    // 사용자 API - /api/auth/** 외에는 모든 요청 인증 필요
                    .requestMatchers("/api/users/**").authenticated()
                    
                    // 나머지 모든 요청은 인증 필요
                    .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 
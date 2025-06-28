package com.example.demo.modules.user.dto;

import com.example.demo.modules.user.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class UserDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserCreateRequest", description = "사용자 생성 요청 DTO")
    public static class CreateRequest {
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
        private String password;

        @NotBlank(message = "이름은 필수입니다")
        private String name;

        private String nickname;
        private String phoneNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserUpdateRequest", description = "사용자 정보 수정 요청 DTO")
    public static class UpdateRequest {
        private String name;
        private String nickname;
        private String phoneNumber;
        private String profileImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserLoginRequest", description = "사용자 로그인 요청 DTO")
    public static class LoginRequest {
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserResponse", description = "사용자 정보 응답 DTO")
    public static class Response {
        private Long id;
        private String email;
        private String name;
        private String nickname;
        private String phoneNumber;
        private String profileImageUrl;
        private Role role;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserLoginResponse", description = "사용자 로그인 응답 DTO")
    public static class LoginResponse {
        private String accessToken;
        @Builder.Default
        private String tokenType = "Bearer";
        private Response user;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserPasswordChangeRequest", description = "사용자 비밀번호 변경 요청 DTO")
    public static class PasswordChangeRequest {
        @NotBlank(message = "현재 비밀번호는 필수입니다")
        private String currentPassword;

        @NotBlank(message = "새 비밀번호는 필수입니다")
        @Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다")
        private String newPassword;
    }
} 
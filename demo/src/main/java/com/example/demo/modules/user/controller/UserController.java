package com.example.demo.modules.user.controller;

import com.example.demo.modules.user.dto.UserDto;
import com.example.demo.modules.user.service.UserService;
import com.example.demo.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이메일 중복")
    })
    @PostMapping("/auth/register")
    public ResponseEntity<UserDto.Response> register(@Valid @RequestBody UserDto.CreateRequest request) {
        try {
            UserDto.Response response = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "로그인", description = "사용자 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<UserDto.LoginResponse> login(@Valid @RequestBody UserDto.LoginRequest request) {
        try {
            UserDto.LoginResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users/me")
    public ResponseEntity<UserDto.Response> getMyInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserDto.Response response = userService.getUserById(userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable Long id) {
        try {
            UserDto.Response response = userService.getUserById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "활성 사용자 목록 조회", description = "모든 활성 사용자 목록을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users")
    public ResponseEntity<List<UserDto.Response>> getAllActiveUsers() {
        List<UserDto.Response> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/users/me")
    public ResponseEntity<UserDto.Response> updateMyInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UserDto.UpdateRequest request) {
        UserDto.Response response = userService.updateUser(userPrincipal.getId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/users/me/password")
    public ResponseEntity<Void> changePassword(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UserDto.PasswordChangeRequest request) {
        try {
            userService.changePassword(userPrincipal.getId(), request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "사용자 비활성화", description = "사용자를 비활성화합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "닉네임으로 사용자 검색", description = "닉네임으로 사용자를 검색합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users/search")
    public ResponseEntity<List<UserDto.Response>> searchUsersByNickname(
            @RequestParam String nickname) {
        List<UserDto.Response> users = userService.searchUsersByNickname(nickname);
        return ResponseEntity.ok(users);
    }
} 
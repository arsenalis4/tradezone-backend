package com.example.demo.modules.chat.controller;

import com.example.demo.modules.chat.dto.ChatDto;
import com.example.demo.modules.chat.service.ChatService;
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
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "채팅 관리 API")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "채팅방 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/rooms")
    public ResponseEntity<ChatDto.RoomResponse> createChatRoom(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ChatDto.RoomCreateRequest request) {
        try {
            ChatDto.RoomResponse response = chatService.createChatRoom(request, userPrincipal.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("채팅방 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "활성 채팅방 목록 조회", description = "모든 활성 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<ChatDto.RoomListResponse> getActiveChatRooms() {
        ChatDto.RoomListResponse response = chatService.getActiveChatRooms();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채팅방 상세 조회", description = "특정 채팅방의 상세 정보를 조회합니다.")
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatDto.RoomResponse> getChatRoom(@PathVariable Long roomId) {
        try {
            ChatDto.RoomResponse response = chatService.getChatRoom(roomId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "채팅방 메시지 목록 조회", description = "특정 채팅방의 메시지 목록을 조회합니다.")
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatDto.MessageResponse>> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<ChatDto.MessageResponse> messages = chatService.getChatMessages(roomId, limit);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "최근 메시지 조회", description = "특정 시간 이후의 메시지를 조회합니다.")
    @GetMapping("/rooms/{roomId}/messages/recent")
    public ResponseEntity<List<ChatDto.MessageResponse>> getRecentMessages(
            @PathVariable Long roomId,
            @RequestParam String since) {
        try {
            LocalDateTime sinceDateTime = LocalDateTime.parse(since);
            List<ChatDto.MessageResponse> messages = chatService.getRecentMessages(roomId, sinceDateTime);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "내가 만든 채팅방 목록", description = "현재 사용자가 생성한 채팅방 목록을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/rooms/my")
    public ResponseEntity<List<ChatDto.RoomResponse>> getMyChatRooms(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ChatDto.RoomResponse> rooms = chatService.getUserChatRooms(userPrincipal.getId());
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "채팅방 참가", description = "채팅방에 참가합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<Void> joinChatRoom(@PathVariable Long roomId) {
        try {
            chatService.joinChatRoom(roomId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방에서 나갑니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long roomId) {
        try {
            chatService.leaveChatRoom(roomId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 
package com.example.demo.modules.chat.dto;

import com.example.demo.modules.chat.entity.ChatMessage;
import com.example.demo.modules.chat.entity.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public class ChatDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ChatRoomCreateRequest", description = "채팅방 생성 요청")
    public static class RoomCreateRequest {
        
        @NotBlank(message = "채팅방 이름은 필수입니다")
        @Size(min = 1, max = 100, message = "채팅방 이름은 1-100자 사이여야 합니다")
        @Schema(description = "채팅방 이름", example = "개발자들의 방")
        private String name;
        
        @Size(max = 500, message = "설명은 500자 이하여야 합니다")
        @Schema(description = "채팅방 설명", example = "개발 관련 이야기를 나누는 방입니다")
        private String description;
        
        @Schema(description = "채팅방 타입", example = "PUBLIC")
        @Builder.Default
        private ChatRoom.RoomType type = ChatRoom.RoomType.PUBLIC;
        
        @Min(value = 2, message = "최대 참가자 수는 2명 이상이어야 합니다")
        @Max(value = 100, message = "최대 참가자 수는 100명 이하여야 합니다")
        @Schema(description = "최대 참가자 수", example = "50")
        @Builder.Default
        private Integer maxParticipants = 50;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ChatRoomResponse", description = "채팅방 응답")
    public static class RoomResponse {
        
        @Schema(description = "채팅방 ID", example = "1")
        private Long id;
        
        @Schema(description = "채팅방 이름", example = "개발자들의 방")
        private String name;
        
        @Schema(description = "채팅방 설명", example = "개발 관련 이야기를 나누는 방입니다")
        private String description;
        
        @Schema(description = "채팅방 타입", example = "PUBLIC")
        private ChatRoom.RoomType type;
        
        @Schema(description = "생성자 ID", example = "1")
        private Long creatorId;
        
        @Schema(description = "생성자 닉네임", example = "개발자123")
        private String creatorNickname;
        
        @Schema(description = "활성 상태", example = "true")
        private Boolean isActive;
        
        @Schema(description = "최대 참가자 수", example = "50")
        private Integer maxParticipants;
        
        @Schema(description = "현재 참가자 수", example = "5")
        private Integer currentParticipants;
        
        @Schema(description = "생성일시")
        private LocalDateTime createdAt;
        
        @Schema(description = "수정일시")
        private LocalDateTime updatedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ChatMessageRequest", description = "채팅 메시지 전송 요청")
    public static class MessageRequest {
        
        @NotBlank(message = "메시지 내용은 필수입니다")
        @Size(min = 1, max = 1000, message = "메시지는 1-1000자 사이여야 합니다")
        @Schema(description = "메시지 내용", example = "안녕하세요!")
        private String content;
        
        @NotNull(message = "채팅방 ID는 필수입니다")
        @Schema(description = "채팅방 ID", example = "1")
        private Long roomId;
        
        @Schema(description = "메시지 타입", example = "CHAT")
        @Builder.Default
        private ChatMessage.MessageType type = ChatMessage.MessageType.CHAT;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ChatMessageResponse", description = "채팅 메시지 응답")
    public static class MessageResponse {
        
        @Schema(description = "메시지 ID", example = "1")
        private Long id;
        
        @Schema(description = "메시지 내용", example = "안녕하세요!")
        private String content;
        
        @Schema(description = "메시지 타입", example = "CHAT")
        private ChatMessage.MessageType type;
        
        @Schema(description = "발신자 ID", example = "1")
        private Long senderId;
        
        @Schema(description = "발신자 닉네임", example = "개발자123")
        private String senderNickname;
        
        @Schema(description = "채팅방 ID", example = "1")
        private Long roomId;
        
        @Schema(description = "채팅방 이름", example = "개발자들의 방")
        private String roomName;
        
        @Schema(description = "생성일시")
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "ChatRoomListResponse", description = "채팅방 목록 응답")
    public static class RoomListResponse {
        
        @Schema(description = "채팅방 목록")
        private List<RoomResponse> rooms;
        
        @Schema(description = "전체 개수", example = "10")
        private long totalCount;
    }
} 
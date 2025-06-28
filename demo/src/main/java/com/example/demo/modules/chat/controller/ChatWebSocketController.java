package com.example.demo.modules.chat.controller;

import com.example.demo.modules.chat.dto.ChatDto;
import com.example.demo.modules.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatDto.MessageResponse sendMessage(
            @DestinationVariable Long roomId,
            ChatDto.MessageRequest messageRequest) {
        
        log.info("웹소켓 메시지 수신: Room {}, Content: {}", roomId, messageRequest.getContent());
        
        try {
            // 메시지를 데이터베이스에 저장
            ChatDto.MessageResponse savedMessage = chatService.saveMessage(messageRequest, 1L); // TODO: 실제 사용자 ID 사용
            
            log.info("메시지 전송 완료: Room {}, Message ID: {}", roomId, savedMessage.getId());
            return savedMessage;
            
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("메시지 전송에 실패했습니다");
        }
    }

    @MessageMapping("/chat/{roomId}/join")
    @SendTo("/topic/room/{roomId}")
    public ChatDto.MessageResponse joinRoom(
            @DestinationVariable Long roomId,
            ChatDto.MessageRequest joinMessage) {
        
        log.info("사용자 채팅방 참가: Room {}", roomId);
        
        try {
            // 참가자 수 증가
            chatService.joinChatRoom(roomId);
            
            // JOIN 메시지 저장 및 브로드캐스트
            ChatDto.MessageResponse joinResponse = chatService.saveMessage(joinMessage, 1L); // TODO: 실제 사용자 ID 사용
            
            return joinResponse;
            
        } catch (Exception e) {
            log.error("채팅방 참가 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 참가에 실패했습니다");
        }
    }

    @MessageMapping("/chat/{roomId}/leave")
    @SendTo("/topic/room/{roomId}")
    public ChatDto.MessageResponse leaveRoom(
            @DestinationVariable Long roomId,
            ChatDto.MessageRequest leaveMessage) {
        
        log.info("사용자 채팅방 나가기: Room {}", roomId);
        
        try {
            // 참가자 수 감소
            chatService.leaveChatRoom(roomId);
            
            // LEAVE 메시지 저장 및 브로드캐스트
            ChatDto.MessageResponse leaveResponse = chatService.saveMessage(leaveMessage, 1L); // TODO: 실제 사용자 ID 사용
            
            return leaveResponse;
            
        } catch (Exception e) {
            log.error("채팅방 나가기 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("채팅방 나가기에 실패했습니다");
        }
    }

    // 특정 사용자에게 직접 메시지 전송
    public void sendMessageToUser(String username, ChatDto.MessageResponse message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/messages", message);
    }

    // 특정 채팅방에 메시지 브로드캐스트
    public void broadcastToRoom(Long roomId, ChatDto.MessageResponse message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }
} 
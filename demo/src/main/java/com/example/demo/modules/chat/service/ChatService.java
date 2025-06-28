package com.example.demo.modules.chat.service;

import com.example.demo.modules.chat.dto.ChatDto;
import com.example.demo.modules.chat.entity.ChatMessage;
import com.example.demo.modules.chat.entity.ChatRoom;
import com.example.demo.modules.chat.repository.ChatMessageRepository;
import com.example.demo.modules.chat.repository.ChatRoomRepository;
import com.example.demo.modules.user.entity.User;
import com.example.demo.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // 채팅방 생성
    public ChatDto.RoomResponse createChatRoom(ChatDto.RoomCreateRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + creatorId));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .creator(creator)
                .maxParticipants(request.getMaxParticipants())
                .currentParticipants(1) // 생성자가 첫 번째 참가자
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        log.info("새로운 채팅방이 생성되었습니다: {} (ID: {})", savedRoom.getName(), savedRoom.getId());

        return convertToRoomResponse(savedRoom);
    }

    // 활성 채팅방 목록 조회
    @Transactional(readOnly = true)
    public ChatDto.RoomListResponse getActiveChatRooms() {
        List<ChatRoom> rooms = chatRoomRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        List<ChatDto.RoomResponse> roomResponses = rooms.stream()
                .map(this::convertToRoomResponse)
                .collect(Collectors.toList());

        return ChatDto.RoomListResponse.builder()
                .rooms(roomResponses)
                .totalCount(roomResponses.size())
                .build();
    }

    // 채팅방 상세 조회
    @Transactional(readOnly = true)
    public ChatDto.RoomResponse getChatRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다: " + roomId));

        return convertToRoomResponse(room);
    }

    // 채팅방 메시지 저장
    public ChatDto.MessageResponse saveMessage(ChatDto.MessageRequest request, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + senderId));

        ChatRoom chatRoom = chatRoomRepository.findByIdAndIsActiveTrue(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다: " + request.getRoomId()));

        ChatMessage message = ChatMessage.builder()
                .content(request.getContent())
                .type(request.getType())
                .sender(sender)
                .chatRoom(chatRoom)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        log.debug("새로운 메시지가 저장되었습니다: Room {}, Sender {}", 
                 chatRoom.getId(), sender.getNickname());

        return convertToMessageResponse(savedMessage);
    }

    // 채팅방 메시지 목록 조회
    @Transactional(readOnly = true)
    public List<ChatDto.MessageResponse> getChatMessages(Long roomId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<ChatMessage> messages = chatMessageRepository
                .findTop50ByChatRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(roomId, pageable);

        // 최신 메시지부터 가져왔으므로 순서를 뒤집어서 오래된 것부터 정렬
        return messages.stream()
                .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    // 최근 메시지 조회 (특정 시간 이후)
    @Transactional(readOnly = true)
    public List<ChatDto.MessageResponse> getRecentMessages(Long roomId, LocalDateTime since) {
        List<ChatMessage> messages = chatMessageRepository.findRecentMessages(roomId, since);
        return messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    // 사용자가 만든 채팅방 목록
    @Transactional(readOnly = true)
    public List<ChatDto.RoomResponse> getUserChatRooms(Long userId) {
        List<ChatRoom> rooms = chatRoomRepository.findByCreatorIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
        return rooms.stream()
                .map(this::convertToRoomResponse)
                .collect(Collectors.toList());
    }

    // 채팅방 참가자 수 증가
    public void joinChatRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다: " + roomId));

        if (room.getCurrentParticipants() >= room.getMaxParticipants()) {
            throw new RuntimeException("채팅방 참가자 수가 가득찼습니다");
        }

        room.setCurrentParticipants(room.getCurrentParticipants() + 1);
        chatRoomRepository.save(room);
        log.info("사용자가 채팅방에 참가했습니다: Room {}, 현재 참가자 수: {}", 
                roomId, room.getCurrentParticipants());
    }

    // 채팅방 참가자 수 감소
    public void leaveChatRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다: " + roomId));

        if (room.getCurrentParticipants() > 0) {
            room.setCurrentParticipants(room.getCurrentParticipants() - 1);
            chatRoomRepository.save(room);
            log.info("사용자가 채팅방에서 나갔습니다: Room {}, 현재 참가자 수: {}", 
                    roomId, room.getCurrentParticipants());
        }
    }

    // 엔티티를 DTO로 변환
    private ChatDto.RoomResponse convertToRoomResponse(ChatRoom room) {
        return ChatDto.RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .type(room.getType())
                .creatorId(room.getCreator().getId())
                .creatorNickname(room.getCreator().getNickname())
                .isActive(room.getIsActive())
                .maxParticipants(room.getMaxParticipants())
                .currentParticipants(room.getCurrentParticipants())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    private ChatDto.MessageResponse convertToMessageResponse(ChatMessage message) {
        return ChatDto.MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .type(message.getType())
                .senderId(message.getSender().getId())
                .senderNickname(message.getSender().getNickname())
                .roomId(message.getChatRoom().getId())
                .roomName(message.getChatRoom().getName())
                .createdAt(message.getCreatedAt())
                .build();
    }
} 
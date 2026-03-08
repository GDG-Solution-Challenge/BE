package com.example.mamatolmi.domain.chat.controller;


import com.example.mamatolmi.domain.chat.dto.request.ChatRequestDTO;
import com.example.mamatolmi.domain.chat.dto.response.ChatResponseDTO;
import com.example.mamatolmi.domain.chat.service.ChatService;
import com.example.mamatolmi.global.apiPayload.ApiResponse;
import com.example.mamatolmi.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping("/rooms")
    public ApiResponse<ChatResponseDTO.ChatRoomCreateResult> createChatRoom(@RequestBody ChatRequestDTO.ChatRoomCreate chatRoomCreate) {
        return ApiResponse.onSuccess(GeneralSuccessCode._OK,chatService.createChatRoom(chatRoomCreate));
    }

    // 질문하기 API
    // 해석: {roomId}번 채팅방(rooms)에 메시지(messages)를 생성
    @PostMapping("/rooms/{roomId}/messages")
    public ApiResponse<ChatResponseDTO> sendMessage(
            @PathVariable("roomId") Long roomId,
            @RequestBody ChatRequestDTO.ChatMessage chatMessage
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode._OK, chatService.sendMessage(roomId, chatMessage));
    }
}

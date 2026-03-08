package com.example.mamatolmi.domain.chat.controller;

import com.example.mamatolmi.domain.chat.dto.request.ChatRequestDTO;
import com.example.mamatolmi.domain.chat.dto.response.ChatResponseDTO;
import com.example.mamatolmi.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ChatControllerDocs {
    // 채팅방 생성
    @Operation(
            summary = "채팅방 생성"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping("/rooms")
    ApiResponse<ChatResponseDTO.ChatRoomCreateResult> createChatRoom(@RequestBody ChatRequestDTO.ChatRoomCreate chatRoomCreate);

    // 질문하기
    // {roomId}번 채팅방(rooms)에 메시지(messages)를 생성
    @Operation(
            summary = "ai에게 대화 이어나가고 질문과 답변을 데이터베이스에 저장"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping("/rooms/{roomId}/messages")
    ApiResponse<ChatResponseDTO> sendMessage(
            @PathVariable("roomId") Long roomId,
            @RequestBody ChatRequestDTO.ChatMessage chatMessage
    );

    // 채팅방 기록 보여주기
    @Operation(
            summary = "채팅방별 기록 보여주기"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패")
    })
    @GetMapping("/rooms/{roomId}/messages")
    ApiResponse<ChatResponseDTO.ChatHistoryResult> getChatHistory(
            @PathVariable("roomId") Long roomId);
}

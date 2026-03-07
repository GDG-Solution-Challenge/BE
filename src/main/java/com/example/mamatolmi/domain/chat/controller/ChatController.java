package com.example.mamatolmi.domain.chat.controller;


import com.example.mamatolmi.domain.chat.dto.request.ChatRequestDTO;
import com.example.mamatolmi.domain.chat.dto.response.ChatResponseDTO;
import com.example.mamatolmi.domain.chat.service.ChatService;
import com.example.mamatolmi.global.apiPayload.ApiResponse;
import com.example.mamatolmi.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    // 질문하기 API
    @PostMapping("/send")
    public ApiResponse<ChatResponseDTO> sendMessage(@RequestBody ChatRequestDTO requestDTO) {
        return ApiResponse.onSuccess(GeneralSuccessCode._OK, chatService.sendMessage(requestDTO));
    }
}

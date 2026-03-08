package com.example.mamatolmi.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatResponseDTO {
    private String response; // AI의 답변


    public record ChatRoomCreateResult(
            Long chatRoomId,
            LocalDateTime createdAt
    ) {}

}

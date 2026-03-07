package com.example.mamatolmi.domain.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequestDTO {
    private Long chatRoomId; // 어느 채팅방에서 보낸 건지
    private String message;  // 사용자가 입력한 질문

}

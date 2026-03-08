package com.example.mamatolmi.domain.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequestDTO {
    public record ChatRoomCreate(
            Long userId,
            Long kidsNoteId
    ){}
    public record ChatMessage(
            String message  // 사용자가 입력한 질문
    ){}

}

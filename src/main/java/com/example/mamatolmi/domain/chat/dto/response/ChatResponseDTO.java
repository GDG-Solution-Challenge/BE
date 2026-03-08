package com.example.mamatolmi.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ChatResponseDTO {
    private String response; // AI의 답변

    public record ChatRoomCreateResult(
            Long chatRoomId,
            LocalDateTime createdAt
    ) {}

    // 메시지 한 건의 상세 정보
    public record ChatMessageDetail(
            Long messageId,
            String sender,
            String content,
            LocalDateTime createdAt
    ) {}

    // 전체 대화 내역 리스트
    public record ChatHistoryResult(
            List<ChatMessageDetail> messages
    ) {}

}

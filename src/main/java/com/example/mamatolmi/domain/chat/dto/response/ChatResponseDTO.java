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

    // 자녀 1명에 대한 채팅방 묶음
    public record ChildChatGroup(
            Long childId,
            String childName,
            List<ChatRoomSummary> chatRooms
    ) {}

    // 개별 채팅방 정보 (날짜와 방 번호)
    public record ChatRoomSummary(
            Long roomId,
            String date // "3/02" 형태로 포맷팅해서 내려줌
    ) {}

    // 최종 응답 DTO (자녀 그룹들의 리스트)
    public record ChatSidebarResult(
            List<ChildChatGroup> childChatGroups
    ) {}

}

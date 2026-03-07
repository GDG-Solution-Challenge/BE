package com.example.mamatolmi.domain.chat.entity;

import com.example.mamatolmi.domain.chat.enums.SenderRole;
import com.example.mamatolmi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "chatmessage")
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderRole sender; // USER 인지 ASSISTANT(AI) 인지

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 대화 내용

    @Builder
    public ChatMessage(ChatRoom chatRoom, SenderRole sender, String content) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
    }

}

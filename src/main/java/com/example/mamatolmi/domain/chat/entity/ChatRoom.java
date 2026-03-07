package com.example.mamatolmi.domain.chat.entity;

import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "chatroom")
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 키즈노트(알림장)에 대한 채팅방인지 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kidsnote_id")
    private KidsNote kidsNote;

    // 누가 만든 채팅방인지 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 양방향 매핑 (채팅방을 지우면 안에 있는 메시지도 다 지워지게 설정)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();



}

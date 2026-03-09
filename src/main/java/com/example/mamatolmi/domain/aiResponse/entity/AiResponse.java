package com.example.mamatolmi.domain.aiResponse.entity;

import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import com.example.mamatolmi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ai_response")
public class AiResponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "todo_list", columnDefinition = "TEXT")
    private String todoList;

    @Column(name = "guide", columnDefinition = "TEXT")
    private String guide;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kidsnote_id", nullable = false)
    private KidsNote kidsNote;

    @Builder
    public AiResponse(String summary, String todoList, String guide, KidsNote kidsNote) {
        this.summary = summary;
        this.todoList = todoList;
        this.guide = guide;
        this.kidsNote = kidsNote;
    }

    public void update(String summary, String todoList, String guide){
        this.summary = summary;
        this.todoList = todoList;
        this.guide = guide;
    }
}

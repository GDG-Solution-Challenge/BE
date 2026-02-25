package com.example.mamatolmi.domain.kidsNote.entity;

import com.example.mamatolmi.domain.kid.entity.Kid;
import com.example.mamatolmi.domain.kidsNote.enums.SourceType;
import com.example.mamatolmi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "kidsnote")
public class KidsNote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "kid_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Kid kid;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private SourceType sourceType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String rawText;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public static KidsNote createFromImage(Kid kid, String rawText, LocalDateTime expiresAt) {
        KidsNote note = new KidsNote();
        note.kid = kid;
        note.sourceType = SourceType.IMAGE;
        note.rawText = rawText;
        note.expiresAt = expiresAt;
        return note;
    }

}

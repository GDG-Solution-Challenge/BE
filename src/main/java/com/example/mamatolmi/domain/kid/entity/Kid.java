package com.example.mamatolmi.domain.kid.entity;

import com.example.mamatolmi.domain.kid.enums.Gender;
import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "kid")
public class Kid extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 부모(User)의 자녀인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 자녀 이름
    @Column(nullable = false, length = 50)
    private String name;

    // 자녀 성별
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    // 자녀 나이
    @Column(nullable = false)
    private LocalDate birthDate;

    // 성향 분석
    @Column(columnDefinition = "TEXT")
    private String analysis;

    // 강점 리스트 ("창의력", "사회성", "리더십" 등)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "kid_strengths", joinColumns = @JoinColumn(name = "kid_id"))
    @Column(name = "strength")
    private List<String> strengths = new ArrayList<>();

    // 종합 피드백
    @Column(columnDefinition = "TEXT")
    private String comprehensiveFeedback;

    @Column(name = "last_analyzed_at")
    private LocalDateTime lastAnalyzedAt; // 마지막 분석 시간

    // 분석 결과 업데이트 메서드
    public void updateProfileAnalysis(String analysis, List<String> strengths, String comprehensiveFeedback) {
        this.analysis = analysis;
        // 기존 컬렉션을 비우고 새 데이터로 채웁니다 (JPA ElementCollection 특성 고려)
        this.strengths.clear();
        this.strengths.addAll(strengths);
        this.comprehensiveFeedback = comprehensiveFeedback;
        this.lastAnalyzedAt = LocalDateTime.now();
    }

    @Builder
    public Kid(User user, String name, Gender gender, LocalDate birthDate) {
        this.user = user;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
    }

}

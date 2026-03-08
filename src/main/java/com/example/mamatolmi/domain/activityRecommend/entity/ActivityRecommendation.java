package com.example.mamatolmi.domain.activityRecommend.entity;

import com.example.mamatolmi.domain.checklist.enums.ChecklistType;
import com.example.mamatolmi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "activity_recommendation")
public class ActivityRecommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChecklistType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
}

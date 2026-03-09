package com.example.mamatolmi.domain.checklist.entity;

import com.example.mamatolmi.domain.checklist.enums.ChecklistType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "checklist")
public class Checklist{

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

    @Column(name = "is_early", nullable = false)
    private Boolean isEarly;
}

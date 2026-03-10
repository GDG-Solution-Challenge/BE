package com.example.mamatolmi.domain.user.entity;

import com.example.mamatolmi.domain.user.enums.KoreanLevelType;
import com.example.mamatolmi.domain.user.enums.ResponseLanguageType;
import com.example.mamatolmi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "korean_level")
    private KoreanLevelType koreanLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_language")
    private ResponseLanguageType responseLanguage;

    public User(String name, String email, KoreanLevelType koreanLevel, ResponseLanguageType responseLanguage) {
        this.name = name;
        this.email = email;
        this.koreanLevel = koreanLevel;
        this.responseLanguage = responseLanguage;
    }

    public void updateSetting(KoreanLevelType level, ResponseLanguageType language){
        this.koreanLevel = level;
        this.responseLanguage = language;
    }
}

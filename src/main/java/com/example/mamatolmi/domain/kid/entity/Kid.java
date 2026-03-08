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

    @Builder
    public Kid(User user, String name, Gender gender, LocalDate birthDate) {
        this.user = user;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
    }

}

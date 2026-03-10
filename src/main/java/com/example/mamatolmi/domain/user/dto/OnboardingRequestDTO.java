package com.example.mamatolmi.domain.user.dto;

import com.example.mamatolmi.domain.user.enums.KoreanLevelType;
import com.example.mamatolmi.domain.user.enums.ResponseLanguageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingRequestDTO {

    private Long userId;

    private KoreanLevelType koreanLevel;

    private ResponseLanguageType responseLanguage;
}

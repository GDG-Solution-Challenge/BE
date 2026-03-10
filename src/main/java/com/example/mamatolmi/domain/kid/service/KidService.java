package com.example.mamatolmi.domain.kid.service;

import com.example.mamatolmi.domain.kid.dto.request.KidRequestDTO;
import com.example.mamatolmi.domain.kid.dto.response.KidResponseDTO;
import com.example.mamatolmi.domain.kid.entity.Kid;
import com.example.mamatolmi.domain.kid.exception.KidException;
import com.example.mamatolmi.domain.kid.exception.code.KidErrorCode;
import com.example.mamatolmi.domain.kid.repository.KidRepository;
import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.domain.user.exception.UserException;
import com.example.mamatolmi.domain.user.exception.code.UserErrorCode;
import com.example.mamatolmi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class KidService {
    private final UserRepository userRepository;
    private final KidRepository kidRepository;

    @Transactional
    public KidResponseDTO.KidCreateResult createKid(Long userId, KidRequestDTO.KidCreate request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 2. 받은 정보로 자녀(Kid) 엔티티 생성
        Kid kid = Kid.builder()
                .user(user)
                .name(request.name())
                .gender(request.gender())
                .birthDate(request.birthDate())
                .build();

        // 3. DB에 저장
        Kid savedKid = kidRepository.save(kid);

        // 4. 응답 DTO(record)로 변환하여 반환
        return new KidResponseDTO.KidCreateResult(
                savedKid.getId(),
                savedKid.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public KidResponseDTO.KidDashboardResult getKidDashboard(Long kidId) {
        // 1. 자녀 엔티티 조회
        Kid kid = kidRepository.findById(kidId)
                .orElseThrow(() -> new KidException(KidErrorCode.KID_NOT_FOUND));

        // 2. DTO로 변환하여 반환
        return new KidResponseDTO.KidDashboardResult(
                kid.getId(),
                kid.getName(),
                kid.getAnalysis() != null ? kid.getAnalysis() : "아직 분석된 성향이 없습니다.",
                kid.getStrengths() != null ? kid.getStrengths() : new ArrayList<>(),
                kid.getComprehensiveFeedback() != null ? kid.getComprehensiveFeedback() : "기록이 쌓이면 AI가 피드백을 분석해 드려요!"
        );
    }
}

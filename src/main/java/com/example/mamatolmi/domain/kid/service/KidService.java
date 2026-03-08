package com.example.mamatolmi.domain.kid.service;

import com.example.mamatolmi.domain.kid.dto.request.KidRequestDTO;
import com.example.mamatolmi.domain.kid.dto.response.KidResponseDTO;
import com.example.mamatolmi.domain.kid.entity.Kid;
import com.example.mamatolmi.domain.kid.repository.KidRepository;
import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.domain.user.exception.UserException;
import com.example.mamatolmi.domain.user.exception.code.UserErrorCode;
import com.example.mamatolmi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

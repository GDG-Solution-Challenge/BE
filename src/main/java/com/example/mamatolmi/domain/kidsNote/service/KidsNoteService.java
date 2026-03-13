package com.example.mamatolmi.domain.kidsNote.service;

import com.example.mamatolmi.domain.kid.entity.Kid;
import com.example.mamatolmi.domain.kid.exception.KidException;
import com.example.mamatolmi.domain.kid.exception.code.KidErrorCode;
import com.example.mamatolmi.domain.kid.repository.KidRepository;
import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import com.example.mamatolmi.domain.kidsNote.repository.KidsNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KidsNoteService {
    private final KidsNoteRepository kidsNoteRepository;
    private final KidRepository kidRepository;
    private final GeminiOcrClient ocrClient;
    private static final int TTL_DAYS = 7;

    // 1. rawText 추출, 저장
    // 이미지
    @Transactional
    public Long createKidsNote(Long kidId, MultipartFile image) throws IOException {
        Kid kid = kidRepository.findById(kidId)
                .orElseThrow(()->new KidException(KidErrorCode.KID_NOT_FOUND));

        // 권한 검증: 이 아이의 부모가 현재 로그인한 유저가 맞는지
//        if (!kid.getUser().getId().equals(userId)) {
//            throw new UserException(UserErrorCode.USER_NOT_AUTHORIZED);
//        }


        // 1) Gemini로 텍스트 추출
        String rawText = ocrClient.extractText(image);

        // 2) 만료시간
        // TODO
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(TTL_DAYS);

        // 3) 저장
        KidsNote note = KidsNote.createFromImage(kid, rawText,expiresAt);
        KidsNote saved = kidsNoteRepository.save(note);
        return saved.getId();

    }

    // 텍스트
    @Transactional
    public Long createFromText(Long kidId, String text) {
        Kid kid = kidRepository.findById(kidId)
                .orElseThrow(()->new KidException(KidErrorCode.KID_NOT_FOUND));

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(TTL_DAYS);

        KidsNote kidsNote = KidsNote.createFromText(kid, text, expiresAt);
        return kidsNoteRepository.save(kidsNote).getId();

    }

}

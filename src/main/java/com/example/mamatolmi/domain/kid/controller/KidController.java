package com.example.mamatolmi.domain.kid.controller;

import com.example.mamatolmi.domain.kid.dto.request.KidRequestDTO;
import com.example.mamatolmi.domain.kid.dto.response.KidResponseDTO;
import com.example.mamatolmi.domain.kid.service.KidService;
import com.example.mamatolmi.global.apiPayload.ApiResponse;
import com.example.mamatolmi.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KidController {
    private final KidService kidService;

    // 자녀 프로필 생성
    // {userId}번 유저의 자녀(kids)를 생성
    @PostMapping("/users/{userId}/kids")
    public ApiResponse<KidResponseDTO.KidCreateResult> createKid(
            @PathVariable("userId") Long userId,
            @RequestBody KidRequestDTO.KidCreate request) {

        return ApiResponse.onSuccess(GeneralSuccessCode._OK, kidService.createKid(userId, request));
    }
}

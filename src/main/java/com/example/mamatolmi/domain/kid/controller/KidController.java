package com.example.mamatolmi.domain.kid.controller;

import com.example.mamatolmi.domain.kid.dto.request.KidRequestDTO;
import com.example.mamatolmi.domain.kid.dto.response.KidResponseDTO;
import com.example.mamatolmi.domain.kid.service.KidService;
import com.example.mamatolmi.global.apiPayload.ApiResponse;
import com.example.mamatolmi.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/kids/{kidId}/analyze")
    public ApiResponse<KidResponseDTO.KidDashboardResult> generateProfileAnalysis(
            @PathVariable("kidId") Long kidId) {

        return ApiResponse.onSuccess(
                GeneralSuccessCode._OK,
                kidService.generateProfileAnalysis(kidId)
        );
    }

    /*
     * 자녀 프로필 대시보드 (요약 정보, 강점, 종합피드백, 주간기록) 조회
     */
    @GetMapping("/kids/{kidId}/dashboard")
    public ApiResponse<KidResponseDTO.KidDashboardResult> getKidDashboard(
            @PathVariable("kidId") Long kidId) {
        return ApiResponse.onSuccess(GeneralSuccessCode._OK, kidService.getKidDashboard(kidId));
    }


    // 키즈 목록 조회 GET /users/:userId/kids  전체 아이 목록 API
    @GetMapping("/users/{userId}/kids")
    public ApiResponse<KidResponseDTO.KidListResult> getKidsList(@PathVariable("userId") Long userId){
        return ApiResponse.onSuccess(GeneralSuccessCode._OK, kidService.getKidsList(userId));
    }

}

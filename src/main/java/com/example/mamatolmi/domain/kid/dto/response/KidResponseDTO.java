package com.example.mamatolmi.domain.kid.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class KidResponseDTO {
    public record KidCreateResult(
            Long kidId,
            LocalDateTime createdAt
    ) {}


    public record KidDashboardResult(
            Long kidId,
            String kidName,
            String analysis,              // 성향 분석
            List<String> strengths,       // 강점 태그들
            String comprehensiveFeedback // 종합 피드백
            // TODO: 나중에 추가할 주간 기록 리스트
            // List<WeeklyRecordDTO> weeklyRecords
    ) {}
}

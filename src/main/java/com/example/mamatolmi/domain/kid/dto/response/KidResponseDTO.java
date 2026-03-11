package com.example.mamatolmi.domain.kid.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class KidResponseDTO {
    public record KidCreateResult(
            Long kidId,
            LocalDateTime createdAt
    ) {}

    public record AiProfileResult(
            String analysis,
            List<String> strengths,
            String comprehensiveFeedback
    ) {}


    public record KidDashboardResult(
            Long kidId,
            String kidName,
            String analysis,              // 성향 분석
            List<String> strengths,       // 강점 태그들
            String comprehensiveFeedback, // 종합 피드백
            List<DailyRecord> weeklyRecords // 주간 기록 리스트
    ) {}


    // 이번 주 전체 리스트
    public record WeeklyRecordList(
            List<DailyRecord> records // 7개의 객체 (월~일)
    ) {}

    public record DailyRecord(
            String dayOfWeek,  // "월"
            String date,       // "02/17"
            boolean isExist,   // 기록 여부
            String content    // "봄 꽃을 그리며 풍부한 색채 감각을 보여준 하루" (핵심 요약)
    ) {}
}

package com.example.mamatolmi.domain.kid.service;

import com.example.mamatolmi.domain.aiResponse.entity.AiResponse;
import com.example.mamatolmi.domain.aiResponse.repository.AiResponseRepository;
import com.example.mamatolmi.domain.kid.dto.request.KidRequestDTO;
import com.example.mamatolmi.domain.kid.dto.response.KidResponseDTO;
import com.example.mamatolmi.domain.kid.entity.Kid;
import com.example.mamatolmi.domain.kid.exception.KidException;
import com.example.mamatolmi.domain.kid.exception.code.KidErrorCode;
import com.example.mamatolmi.domain.kid.repository.KidRepository;
import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import com.example.mamatolmi.domain.kidsNote.repository.KidsNoteRepository;
import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.domain.user.exception.UserException;
import com.example.mamatolmi.domain.user.exception.code.UserErrorCode;
import com.example.mamatolmi.domain.user.repository.UserRepository;
import com.example.mamatolmi.global.ai.gemini.service.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KidService {
    private final UserRepository userRepository;
    private final KidRepository kidRepository;
    private final AiResponseRepository aiResponseRepository;
    private final ObjectMapper objectMapper;
    private final GeminiService geminiService;
    private final KidsNoteRepository kidsNoteRepository;

    // ==========================================
    // 1. 자녀 생성
    // ==========================================
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

    // ==========================================
    // 키즈 목록 조회 GET /users/:userId/kids  전체 아이 목록 API
    // ==========================================
    public KidResponseDTO.KidListResult getKidsList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        List<Kid> kids=kidRepository.findAllByUserId(userId);
        List<KidResponseDTO.KidSummary> kidSummaryList = kids.stream()
                .map(kid -> new KidResponseDTO.KidSummary(
                        kid.getId(),
                        kid.getName(),
                        kid.getGender(),
                        kid.getBirthDate()
                ))
                .toList();

        return new KidResponseDTO.KidListResult(kidSummaryList);

    }

    // ==========================================
    // 2. 자녀 대시보드 전체 조회 (요약 + 이번 주 기록)
    // ==========================================
    @Transactional(readOnly = true)
    public KidResponseDTO.KidDashboardResult getKidDashboard(Long kidId) {
        // 1. 자녀 엔티티 조회
        Kid kid = kidRepository.findById(kidId)
                .orElseThrow(() -> new KidException(KidErrorCode.KID_NOT_FOUND));

        // 2. 주간 기록 7칸 채우기 (아래 private 메서드 호출)
        List<KidResponseDTO.DailyRecord> weeklyRecords = getWeeklyRecords(kidId);

        // 3. 하나로 묶어서 반환!
        return new KidResponseDTO.KidDashboardResult(
                kid.getId(),
                kid.getName(),
                kid.getAnalysis() != null ? kid.getAnalysis() : "아직 분석된 성향이 없습니다.",
                kid.getStrengths() != null ? kid.getStrengths() : new ArrayList<>(),
                kid.getComprehensiveFeedback() != null ? kid.getComprehensiveFeedback() : "기록이 쌓이면 AI가 분석해 드려요!",
                weeklyRecords // ★ 방금 가져온 7일치 리스트 탑재
        );
    }

    // ==========================================
    // 3. AI 성향 분석 수동 생성 (버튼 클릭 시)
    // ==========================================
    @Transactional
    public KidResponseDTO.KidDashboardResult generateProfileAnalysis(Long kidId) {
        Kid kid = kidRepository.findById(kidId)
                .orElseThrow(() -> new IllegalArgumentException("자녀를 찾을 수 없습니다."));

        // 1. 마지막 분석 시간 가져오기 (없으면 2000년으로 세팅해서 모든 기록 가져옴)
        LocalDateTime lastAnalyzed = kid.getLastAnalyzedAt() != null ?
                kid.getLastAnalyzedAt() : LocalDateTime.of(2000, 1, 1, 0, 0);

        // 2. kidsNote 원문 가져오기
        List<KidsNote> unanalyzedNotes = kidsNoteRepository.findByKidIdAndCreatedAtAfterOrderByCreatedAtAsc(kidId, lastAnalyzed);

        if (unanalyzedNotes.isEmpty()) {
            return getKidDashboard(kidId);
        }

        String activitiesText = unanalyzedNotes.stream()
                .map(KidsNote::getRawText)
                .collect(Collectors.joining("\n- ", "- ", ""));

        // 3. 프롬프트 작성
        String prompt = "너는 아동 발달 전문가야. 다음은 아이의 최근 알림장 기록들이야:\n" +
                activitiesText + "\n\n" +
                "이 기록들을 바탕으로 아이의 성향과 피드백을 분석해줘. **단, 모바일 앱 화면에 출력해야 하므로 아래의 글자 수 제한을 무조건 엄격하게 지켜야 해.**\n" +
                "반드시 아래 JSON 형식으로만 대답하고, 마크다운 기호(```)는 절대 쓰지 마.\n" +
                "{\n" +
                "  \"analysis\": \"아이의 이번 주 핵심 성향을 딱 1문장으로 요약 (공백 포함 최대 50자 이내)\",\n" +
                "  \"strengths\": [\"키워드1\", \"키워드2\", \"키워드3\"],\n" +
                "  \"comprehensiveFeedback\": \"부모를 위한 가장 중요한 맞춤 조언 딱 1가지만, 1~2문장으로 다정하게 작성 (공백 포함 최대 80자 이내)\"\n" +
                "}";

        // 4. 공통 서비스로 제미나이 호출
        String rawResponse = geminiService.askGemini(prompt);

        try {
            // 5. JSON 마크다운 찌꺼기 제거 및 DTO 파싱
            String cleanJson = rawResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            KidResponseDTO.AiProfileResult aiResult = objectMapper.readValue(cleanJson, KidResponseDTO.AiProfileResult.class);

            // 6. 자녀 엔티티에 결과 업데이트 (lastAnalyzedAt도 엔티티 내부에서 업데이트)
            kid.updateProfileAnalysis(aiResult.analysis(), aiResult.strengths(), aiResult.comprehensiveFeedback());

        } catch (Exception e) {
            throw new RuntimeException("AI 응답을 파싱하는 중 오류가 발생했습니다.", e);
        }

        // 7. 업데이트가 완료된 최신 대시보드 데이터를 반환
        return getKidDashboard(kidId);
    }

    // ==========================================
    // 4. (내부 메서드) 주간 기록 7일치 계산기
    // ==========================================
    @Transactional(readOnly = true)
    public List<KidResponseDTO.DailyRecord> getWeeklyRecords(Long kidId) {

        // 1. 이번 주 월요일 00:00:00 ~ 일요일 23:59:59 계산
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime startOfWeek = monday.atStartOfDay();
        LocalDateTime endOfWeek = monday.plusDays(6).atTime(23, 59, 59);

        // 2. 해당 기간의 데이터 한 번에 조회
        List<AiResponse> aiResponses = aiResponseRepository.findWeeklyByKidId(kidId, startOfWeek, endOfWeek);

        // 3. 월~일 7칸 채우기
        List<KidResponseDTO.DailyRecord> dailyRecords = new ArrayList<>();
        String[] daysOfWeek = {"월", "화", "수", "목", "금", "토", "일"};

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = monday.plusDays(i);
            String dayStr = daysOfWeek[i];
            String dateStr = currentDate.format(DateTimeFormatter.ofPattern("MM/dd"));

            // 현재 순회 중인 날짜와 일치하는 기록 찾기
            Optional<AiResponse> matchingResponse = aiResponses.stream()
                    .filter(ai -> ai.getKidsNote().getCreatedAt().toLocalDate().equals(currentDate))
                    .findFirst();

            if (matchingResponse.isPresent()) {
                String originalSummary = matchingResponse.get().getSummary();
                String shortContent = originalSummary;

                if (originalSummary != null && originalSummary.contains(".")) {
                    // 첫 번째 마침표(.) 위치를 찾아서 거기까지만 싹둑! (마침표 포함)
                    int dotIndex = originalSummary.indexOf(".");
                    shortContent = originalSummary.substring(0, dotIndex + 1);
                }

                dailyRecords.add(new KidResponseDTO.DailyRecord(
                        dayStr, dateStr, true, shortContent
                ));
            } else {
                // 기록이 없는 날
                dailyRecords.add(new KidResponseDTO.DailyRecord(
                        dayStr, dateStr, false, null
                ));
            }
        }

        return dailyRecords;
    }

}

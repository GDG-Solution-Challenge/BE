package com.example.mamatolmi.domain.aiResponse.service;

import com.example.mamatolmi.domain.activityRecommend.entity.ActivityRecommendation;
import com.example.mamatolmi.domain.activityRecommend.repository.ActivityRecommendationRepository;
import com.example.mamatolmi.domain.aiResponse.client.GeminiClient;
import com.example.mamatolmi.domain.aiResponse.dto.response.AiResDTO;
import com.example.mamatolmi.domain.aiResponse.entity.AiResponse;
import com.example.mamatolmi.domain.aiResponse.repository.AiResponseRepository;
import com.example.mamatolmi.domain.checklist.entity.Checklist;
import com.example.mamatolmi.domain.checklist.repository.ChecklistRepository;
import com.example.mamatolmi.domain.kid.entity.Kid;
import com.example.mamatolmi.domain.kid.enums.Gender;
import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import com.example.mamatolmi.domain.kidsNote.repository.KidsNoteRepository;
import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.domain.user.enums.KoreanLevelType;
import com.example.mamatolmi.domain.user.enums.ResponseLanguageType;
import com.example.mamatolmi.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiResponseService {

    private final KidsNoteRepository kidsNoteRepository;
    private final AiResponseRepository aiResponseRepository;
    private final ChecklistRepository checklistRepository;
    private final ActivityRecommendationRepository activityRecommendationRepository;
    private final GeminiClient geminiClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiResDTO generate(Long kidsNoteId) throws Exception {

        // 1. kidsnote 조회
        KidsNote kidsNote = kidsNoteRepository.findById(kidsNoteId)
                .orElseThrow();

        // 2. 아이 정보 조회
        Kid kid = kidsNote.getKid();
        String kidName = kid.getName();
        Gender gender = kid.getGender();
        LocalDate birthDay = kid.getBirthDate();

        int age = Period.between(birthDay, LocalDate.now()).getYears();

        // 2-1. 사용자 정보 조회
        User user = kid.getUser();
        KoreanLevelType koreanLevel = user.getKoreanLevel();
        ResponseLanguageType responseLanguage = user.getResponseLanguage();

        // 3. 체크리스트 조회
        List<Checklist> checklists = checklistRepository.findByAge(age);

        // 4. 활동 추천 조회
        List<ActivityRecommendation> recommends =
                activityRecommendationRepository.findByAge(age);

        // 5. prompt 생성
        String prompt = buildPrompt(
                kidName,
                gender,
                age,
                koreanLevel,
                responseLanguage,
                kidsNote.getRawText(),
                checklists,
                recommends
        );

        // 6. gemini 호출
        String aiResult;

        try {

            aiResult = geminiClient.generate(prompt);

        } catch (Exception e) {

            log.error("===== GEMINI API ERROR =====");
            log.error("prompt:\n{}", prompt);
            log.error("error:", e);

            throw e;
        }

        // 7. markdown 제거
        aiResult = cleanJson(aiResult);

        // 8. JSON 파싱
        JsonNode node = objectMapper.readTree(aiResult);

        String summary = node.get("summary").asText();
        String todoList = node.get("todoList").asText();
        String guide = node.get("guide").asText();

        // 9. 저장

        AiResponse response = aiResponseRepository
                .findByKidsNote(kidsNote)
                .orElse(null);

        if(response == null){
            response = AiResponse.builder()
                    .summary(summary)
                    .todoList(todoList)
                    .guide(guide)
                    .kidsNote(kidsNote)
                    .build();
        }else {
            response.update(summary, todoList, guide);
        }

        log.info("===== Parsed AI Response =====");
        log.info("summary: {}", summary);
        log.info("todoList: {}", todoList);
        log.info("guide: {}", guide);

        AiResponse saved = aiResponseRepository.save(response);

        return new AiResDTO(
                saved.getSummary(),
                saved.getTodoList(),
                saved.getGuide()
        );
    }


    private String buildPrompt(
            String kidName,
            Gender gender,
            int age,
            KoreanLevelType koreanLevel,
            ResponseLanguageType responseLanguage,
            String kidsNoteJson,
            List<Checklist> checklists,
            List<ActivityRecommendation> recommends
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("이 서비스는 한국에 거주하는 다국적 부모들이\n" +
                "자녀의 어린이집 생활을 이해하고 더 잘 돌볼 수 있도록\n" +
                "도와주는 AI입니다\n\n");

        prompt.append("\n\n### 아이 기본 정보\n");
        prompt.append("이름: ").append(kidName).append("\n");
        prompt.append("성별: ").append(gender).append("\n");
        prompt.append("나이: 만 ").append(age).append("세\n");

        prompt.append("""
다음 JSON은 어린이집에서 작성된 키즈노트 정보를
AI가 분석하여 구조화한 데이터입니다.
""");

        prompt.append("### 키즈노트 분석 JSON\n");
        prompt.append(kidsNoteJson).append("\n\n");

        prompt.append("### 발달 체크리스트\n");
        for (Checklist c : checklists) {
            prompt.append("- ").append(c.getContent()).append("\n");
        }

        prompt.append("\n### 추천 활동\n");
        for (ActivityRecommendation r : recommends) {
            prompt.append("- ").append(r.getContent()).append("\n");
        }

        if(responseLanguage == ResponseLanguageType.KOREAN){
            prompt.append("현재 이 부모의 한국어 실력 정도는 ");
            prompt.append(koreanLevel);
            prompt.append("입니다.\n");
            prompt.append("한국어 실력에 알맞게 이해할 수 있도록 답변하세요.\n");
        }else{
            prompt.append("응답은 반드시 ");
            prompt.append(responseLanguage);
            prompt.append(" 언어로 작성하세요.\n");
        }

        prompt.append("""
위 정보를 종합하여 다음을 수행하세요.

1. 키즈노트를 기반으로 오늘 아이 상태를 간단히 요약하세요.
2. 체크리스트를 참고하여 발달적으로 주의할 점이 있다면 간략하게 요약본에 넣어주세요.
3. todoList에 부모가 챙겨야 할 준비물이나 해야 할 일을 간단히 작성하세요.
4. 활동추천 리스트를 참고하여 부모에게 도움이 되는 양육 가이드를 작성하세요.

작성 규칙
- summary : 최대 3문장
- todoList : 최대 3개 항목을 개행문자를 넣어 한 문장으로 작성하세요.
- guide : 최대 3문장
- 간결하고 부모가 바로 이해할 수 있는 문장으로 작성하세요.
- 체크리스트 전체 설명은 하지 마세요.

반드시 아래 JSON 형식으로만 응답하세요.
모든 항목은 마크업 형식으로 제공고 todoList는 마크업 체크리스트 형식의 한 문장으로 제공하세요.
설명, 마크다운, 코드블록은 절대 포함하지 마세요.

{
  "summary": "오늘 아이 상태 요약",
  "todoList": "부모가 챙겨야 할 준비물이나 해야 할 일",
  "guide": "부모에게 줄 양육 가이드 및 활동 추천"
}

""");

        log.info("===== Gemini Prompt =====");
        log.info(prompt.toString());

        return prompt.toString();
    }


    private String cleanJson(String text) {

        text = text.replace("```json", "");
        text = text.replace("```", "");

        return text.trim();
    }

}

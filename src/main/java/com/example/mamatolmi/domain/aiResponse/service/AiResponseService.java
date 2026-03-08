package com.example.mamatolmi.domain.aiResponse.service;

import com.example.mamatolmi.domain.activityRecommend.entity.ActivityRecommendation;
import com.example.mamatolmi.domain.activityRecommend.repository.ActivityRecommendationRepository;
import com.example.mamatolmi.domain.aiResponse.client.GeminiClient;
import com.example.mamatolmi.domain.aiResponse.entity.AiResponse;
import com.example.mamatolmi.domain.aiResponse.repository.AiResponseRepository;
import com.example.mamatolmi.domain.checklist.entity.Checklist;
import com.example.mamatolmi.domain.checklist.repository.ChecklistRepository;
import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import com.example.mamatolmi.domain.kidsNote.repository.KidsNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiResponseService {

    private final KidsNoteRepository kidsNoteRepository;
    private final AiResponseRepository aiResponseRepository;
    private final ChecklistRepository checklistRepository;
    private final ActivityRecommendationRepository activityRecommendationRepository;
    private final GeminiClient geminiClient;

    public AiResponse generate(Long kidsNoteId) throws Exception {

        // 1. kidsnote 조회
        KidsNote kidsNote = kidsNoteRepository.findById(kidsNoteId)
                .orElseThrow();

        // 2. 나이 조회
        int age = kidsNote.getKid().getAge();

        // 3. 체크리스트 조회
        List<Checklist> checklists = checklistRepository.findByAge(age);

        // 4. 활동 추천 조회
        List<ActivityRecommendation> recommends =
                activityRecommendationRepository.findByAge(age);

        // 5. prompt 생성
        String prompt = buildPrompt(
                kidsNote.getRawText(),
                checklists,
                recommends
        );

        // 6. gemini 호출
        String aiResult = geminiClient.generate(prompt);

        // 7. markdown 제거
        aiResult = cleanJson(aiResult);

        // 8. JSON 파싱
        JsonNode node = objectMapper.readTree(aiResult);

        String summary = node.get("summary").asText();
        String todoList = node.get("todoList").asText();
        String guide = node.get("guide").asText();

        // 9. 저장
        AiResponse response = AiResponse.builder()
                .summary(summary)
                .todoList(todoList)
                .guide(guide)
                .kidsNote(kidsNote)
                .build();

        return aiResponseRepository.save(response);
    }


    private String buildPrompt(
            String kidsNoteJson,
            List<Checklist> checklists,
            List<ActivityRecommendation> recommends
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("다음 정보를 기반으로 부모에게 줄 피드백을 작성하세요.\n\n");

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

        prompt.append("""

반드시 아래 JSON 형식으로만 응답하세요.
설명, 마크다운, 코드블록은 절대 포함하지 마세요.

{
  "summary": "오늘 아이 상태 요약",
  "todoList": "준비물이나 해야 할 일",
  "guide": "부모에게 줄 양육 가이드"
}

""");

        return prompt.toString();
    }


    private String cleanJson(String text) {

        text = text.replace("```json", "");
        text = text.replace("```", "");

        return text.trim();
    }

}

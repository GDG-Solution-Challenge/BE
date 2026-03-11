package com.example.mamatolmi.global.ai.gemini.service;

import com.example.mamatolmi.global.ai.gemini.dto.request.GeminiReqDTO;
import com.example.mamatolmi.global.ai.gemini.dto.response.GeminiResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final RestTemplate restTemplate;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    // 제미나이 API 엔드포인트
    private static final String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";


    /**
     * 제미나이에게 프롬프트를 보내고 텍스트 응답을 받아오는 공통 메서드
     */
    public String askGemini(String prompt) {
        // 1. DTO 구조에 맞게 프롬프트 포장
        GeminiReqDTO.GeminiChatRequest.Part part = new GeminiReqDTO.GeminiChatRequest.Part(prompt);
        GeminiReqDTO.GeminiChatRequest.Content content = new GeminiReqDTO.GeminiChatRequest.Content("user", List.of(part));
        GeminiReqDTO.GeminiChatRequest request = new GeminiReqDTO.GeminiChatRequest(List.of(content));

        // 2. API 호출
        String requestUrl = geminiUrl + "?key=" + geminiApiKey;
        GeminiResDTO.GeminiChatResponse response = restTemplate.postForObject(requestUrl, request, GeminiResDTO.GeminiChatResponse.class);

        // 3. 응답 텍스트 추출
        if (response != null && !response.candidates().isEmpty()) {
            return response.candidates().get(0).content().parts().get(0).text();
        } else {
            throw new RuntimeException("AI가 응답을 생성하지 못했습니다.");
        }
    }
}

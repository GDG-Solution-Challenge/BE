package com.example.mamatolmi.domain.kidsNote.service;

import com.example.mamatolmi.global.ai.gemini.dto.request.GeminiReqDTO;
import com.example.mamatolmi.global.ai.gemini.dto.response.GeminiResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GeminiOcrClient {
    private final WebClient webClient;

    @Value("${gemini.apiKey}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    /*
     * Gemini 멀티모달로 "텍스트만" 추출
     * - 요약/분석 없이 OCR 결과 텍스트만 반환하도록 프롬프트를 강제
     */
    public String extractText(MultipartFile image) throws IOException {
        System.out.println("Gemini URL: https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey);

        String mimeType = image.getContentType();
        if (mimeType==null){
            mimeType = MimeTypeUtils.IMAGE_JPEG_VALUE;
        }
        String base64 = Base64.getEncoder().encodeToString(image.getBytes());

        // Gemini REST endpoint
        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        GeminiReqDTO.GeminiRequest req = buildRequest(mimeType, base64);

        GeminiResDTO geminiResDTO = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(GeminiResDTO.class)
                .block();

        return extractTextFromResponse(geminiResDTO);
    }

    private GeminiReqDTO.GeminiRequest buildRequest(String mimeType, String base64) {
        // 프롬프트: "텍스트만 추출" 강제
        String prompt = """
                You are an OCR assistant.
                Extract ALL readable Korean text from the image.
                Return ONLY the extracted text. Do not summarize, do not add extra words.
                Keep line breaks if present in the image.
                If nothing is readable, return an empty string.
                """;

        GeminiReqDTO.GeminiRequest.Part textPart = GeminiReqDTO.GeminiRequest.Part.ofText(prompt);
        GeminiReqDTO.GeminiRequest.Part imagePart = GeminiReqDTO.GeminiRequest.Part.ofImage(mimeType, base64);

        GeminiReqDTO.GeminiRequest.Content content = new GeminiReqDTO.GeminiRequest.Content(List.of(textPart, imagePart));
        return new GeminiReqDTO.GeminiRequest(List.of(content));
    }


    /*
     * Gemini 응답에서 텍스트 부분만 안전하게 합쳐서 반환
     */
    private String extractTextFromResponse(GeminiResDTO geminiResDTO) {
        if (geminiResDTO == null || geminiResDTO.candidates == null || geminiResDTO.candidates.isEmpty()) {
            return "";
        }

        GeminiResDTO.Candidate first = geminiResDTO.candidates.get(0);
        if (first.content == null || first.content.parts == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (GeminiResDTO.Part p : first.content.parts) {
            if (p != null && p.text != null) {
                sb.append(p.text);
            }
        }

        return sb.toString().trim();
    }
}

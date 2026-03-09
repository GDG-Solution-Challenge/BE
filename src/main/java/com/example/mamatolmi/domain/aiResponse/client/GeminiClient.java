package com.example.mamatolmi.domain.aiResponse.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final WebClient webClient = WebClient.builder().build(); //java -> Http -> Gemini

    @Value("${gemini.apiKey}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    /*
    프롬프트 -> Gemini API -> Response
    * */
    public String generate(String prompt) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        Map response = webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info("===== Gemini API Raw Response =====");
        log.info("{}", response);

        try {

            Map candidate = (Map)((java.util.List)response.get("candidates")).get(0);

            Map content = (Map) candidate.get("content");

            java.util.List parts = (java.util.List) content.get("parts");

            Map part = (Map) parts.get(0);

            return (String) part.get("text");

        } catch (Exception e) {

            log.error("Gemini response parsing failed: {}", response);

            throw new RuntimeException("Gemini response parsing failed", e);
        }
    }
}

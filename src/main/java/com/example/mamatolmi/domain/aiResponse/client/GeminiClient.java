package com.example.mamatolmi.domain.aiResponse.client;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final WebClient webClient = WebClient.builder().build(); //java -> Http -> Gemini

    @Value("${gemini.apiKey}")
    private String apiKey;

    @Value("gemini-2.5-flash")
    private String model;


    /*
    프롬프트 -> Gemini API -> Response
    * */
    public String generate(String prompt){
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

        try {
            return (String) ((Map)((Map)((Map)((java.util.List)response.get("candidates")).get(0))
                    .get("content")).get("parts")).get("text");
        } catch (Exception e) {
            throw new RuntimeException("Gemini response parsing failed");
        }
    }
}

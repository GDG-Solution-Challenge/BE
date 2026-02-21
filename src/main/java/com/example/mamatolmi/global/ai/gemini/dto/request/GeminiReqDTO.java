package com.example.mamatolmi.global.ai.gemini.dto.request;

import java.util.List;

public class GeminiReqDTO {

    public record GeminiRequest(
            List<Content> contents
    ) {

        public record Content(
                List<Part> parts
        ) {}

        public record Part(
                String text,
                InlineData inlineData
        ) {
            public static Part ofText(String text) {
                return new Part(text, null);
            }

            public static Part ofImage(String mimeType, String base64) {
                return new Part(null, new InlineData(mimeType, base64));
            }
        }

        public record InlineData(
                String mimeType,
                String data
        ) {}
    }
}

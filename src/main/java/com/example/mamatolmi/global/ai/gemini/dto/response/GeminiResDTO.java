package com.example.mamatolmi.global.ai.gemini.dto.response;

import java.util.List;

public class GeminiResDTO {
    public List<Candidate> candidates;

    public static class Candidate {
        public Content content;
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }

}

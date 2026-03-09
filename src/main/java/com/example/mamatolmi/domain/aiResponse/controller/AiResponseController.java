package com.example.mamatolmi.domain.aiResponse.controller;

import com.example.mamatolmi.domain.aiResponse.dto.response.AiResDTO;
import com.example.mamatolmi.domain.aiResponse.entity.AiResponse;
import com.example.mamatolmi.domain.aiResponse.service.AiResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai-response")
public class AiResponseController {

    private final AiResponseService aiResponseService;

    @PostMapping("/{kidsNoteId}")
    public AiResDTO generate(@PathVariable("kidsNoteId") Long kidsNoteId) throws Exception {
        return aiResponseService.generate(kidsNoteId);
    }

}

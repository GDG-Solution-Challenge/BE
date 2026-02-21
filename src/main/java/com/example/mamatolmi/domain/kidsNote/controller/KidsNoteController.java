package com.example.mamatolmi.domain.kidsNote.controller;

import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import com.example.mamatolmi.domain.kidsNote.service.KidsNoteService;
import com.example.mamatolmi.global.apiPayload.ApiResponse;
import com.example.mamatolmi.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kidsnotes")
public class KidsNoteController {
    private final KidsNoteService kidsNoteService;

    // 1. rawText 추출-> 저장
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> createKidsNote(
            @RequestParam("kidId") Long kidId,
            @RequestPart("image") MultipartFile image) throws IOException {

        return ApiResponse.onSuccess(GeneralSuccessCode._OK, kidsNoteService.createKidsNote(kidId, image));
    }
}

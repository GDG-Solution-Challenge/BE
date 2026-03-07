package com.example.mamatolmi.domain.kidsNote.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class KidsNoteReqDTO {
    public record KidsNoteTextCreate(
            Long kidId,
            String text
    ) {}
}

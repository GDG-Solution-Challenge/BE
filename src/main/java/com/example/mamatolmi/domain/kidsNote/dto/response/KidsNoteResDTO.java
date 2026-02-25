package com.example.mamatolmi.domain.kidsNote.dto.response;

public class KidsNoteResDTO {

    public record KidsNote(
            Long id,
            String content
    ) {}
}

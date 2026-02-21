package com.example.mamatolmi.domain.kidsNote.exception.code;

import com.example.mamatolmi.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum KidsNoteSuccessCode implements BaseSuccessCode {
    // 201 Created와 200 OK
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}

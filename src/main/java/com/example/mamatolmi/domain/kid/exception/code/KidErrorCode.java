package com.example.mamatolmi.domain.kid.exception.code;

import com.example.mamatolmi.global.apiPayload.code.BaseErrorCode;
import com.example.mamatolmi.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum KidErrorCode implements BaseErrorCode {
    KID_NOT_FOUND(HttpStatus.NOT_FOUND, "KID404_1", "해당 아이를 찾을 수 없습니다.");
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}

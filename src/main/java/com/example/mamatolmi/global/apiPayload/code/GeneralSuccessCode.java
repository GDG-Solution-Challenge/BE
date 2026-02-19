package com.example.mamatolmi.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode {

    // 가장 일반적인 성공 응답 (200 OK)
    _OK(HttpStatus.OK, "COMMON200", "요청에 성공하였습니다."),

    // 새로운 리소스가 생성되었을 때 (201 Created)
    _CREATED(HttpStatus.CREATED, "COMMON201", "리소스 생성이 완료되었습니다."),

    // 삭제 요청이 성공했으나 반환할 데이터가 없을 때 (204 No Content)
    _NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON204", "성공적으로 처리되었으나 반환할 내용이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

package com.example.mamatolmi.domain.chat.exception.code;

import com.example.mamatolmi.global.apiPayload.code.BaseErrorCode;
import com.example.mamatolmi.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements BaseErrorCode {
    ChAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATROOM404_1", "존재하지 않는 채팅방입니다.");
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}

package com.example.mamatolmi.domain.kid.exception;

import com.example.mamatolmi.global.apiPayload.code.BaseErrorCode;
import com.example.mamatolmi.global.apiPayload.exception.GeneralException;

public class KidException extends GeneralException {
    public KidException(BaseErrorCode code) {
        super(code);
    }
}

package com.example.mamatolmi.domain.user.exception;

import com.example.mamatolmi.global.apiPayload.code.BaseErrorCode;
import com.example.mamatolmi.global.apiPayload.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(BaseErrorCode code) {
        super(code);
    }
}

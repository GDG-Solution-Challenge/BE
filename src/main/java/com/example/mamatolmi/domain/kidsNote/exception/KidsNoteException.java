package com.example.mamatolmi.domain.kidsNote.exception;

import com.example.mamatolmi.global.apiPayload.code.BaseErrorCode;
import com.example.mamatolmi.global.apiPayload.exception.GeneralException;

public class KidsNoteException extends GeneralException {
    public KidsNoteException(BaseErrorCode code) {
        super(code);
    }
}

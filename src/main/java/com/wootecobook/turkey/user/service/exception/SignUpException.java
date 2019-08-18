package com.wootecobook.turkey.user.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignUpException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(SignUpException.class);

    public static final String SIGN_UP_FAIL_MESSAGE = "회원가입에 실패했습니다. : ";

    public SignUpException(String message) {
        super(SIGN_UP_FAIL_MESSAGE + message);
        log.error(SIGN_UP_FAIL_MESSAGE + message);
    }

}

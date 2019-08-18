package com.wootecobook.turkey.login.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFailException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(LoginFailException.class);

    public static final String LOGIN_FAIL_MESSAGE = "로그인 실패 : ";

    public LoginFailException(String message) {
        super(LOGIN_FAIL_MESSAGE + message);
        log.error(LOGIN_FAIL_MESSAGE + message);
    }
}

package com.wootecobook.turkey.user.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMismatchException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(UserMismatchException.class);

    public static final String USER_MISMATCH_MESSAGE = "일치하지 않는 유저입니다.";

    public UserMismatchException() {
        super(USER_MISMATCH_MESSAGE);
        log.error(USER_MISMATCH_MESSAGE);
    }

}

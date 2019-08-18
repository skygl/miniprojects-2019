package com.wootecobook.turkey.user.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotFoundUserException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(NotFoundUserException.class);

    public static final String NOT_FOUND_USER_MESSAGE = "유저를 찾을수 없습니다.";

    public NotFoundUserException() {
        super(NOT_FOUND_USER_MESSAGE);
        log.error(NOT_FOUND_USER_MESSAGE);
    }
}

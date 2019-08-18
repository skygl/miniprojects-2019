package com.wootecobook.turkey.user.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDeleteException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(UserDeleteException.class);

    public static final String USER_DELETE_FAIL_MESSAGE = "회원 탈퇴에 실패했습니다.";

    public UserDeleteException() {
        super(USER_DELETE_FAIL_MESSAGE);
        log.error(USER_DELETE_FAIL_MESSAGE);
    }

}

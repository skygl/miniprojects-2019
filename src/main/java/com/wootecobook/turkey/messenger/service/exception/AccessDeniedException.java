package com.wootecobook.turkey.messenger.service.exception;

public class AccessDeniedException extends RuntimeException {

    private static final String ACCESS_DENIED_MESSAGE = "접근 권한이 없습니다";

    public AccessDeniedException() {
        super(ACCESS_DENIED_MESSAGE);
    }
}

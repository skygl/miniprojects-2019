package com.wootecobook.turkey.post.service.exception;

public class NotFoundPostException extends RuntimeException {

    private static final String NOT_FOUND_POST_ERROR_MESSAGE = "존재하지 않는 포스트입니다.";

    public NotFoundPostException() {
        super(NOT_FOUND_POST_ERROR_MESSAGE);
    }
}

package com.wootecobook.turkey.post.domain.exception;

public class InvalidPostException extends RuntimeException {

    private static final String NOT_POSTING_ERROR_MESSAGE = "포스팅 할 수 없습니다.";

    public InvalidPostException() {
        super(NOT_POSTING_ERROR_MESSAGE);
    }

}

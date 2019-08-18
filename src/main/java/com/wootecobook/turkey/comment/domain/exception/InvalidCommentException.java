package com.wootecobook.turkey.comment.domain.exception;

public class InvalidCommentException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "Comment 를 생성할 수 없습니다. ";

    public InvalidCommentException(String message) {
        super(DEFAULT_MESSAGE + message);
    }
}

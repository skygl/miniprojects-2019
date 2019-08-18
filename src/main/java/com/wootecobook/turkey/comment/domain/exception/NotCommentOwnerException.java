package com.wootecobook.turkey.comment.domain.exception;

public class NotCommentOwnerException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "작성자가 아닙니다.";

    public NotCommentOwnerException() {
        super(DEFAULT_MESSAGE);
    }
}

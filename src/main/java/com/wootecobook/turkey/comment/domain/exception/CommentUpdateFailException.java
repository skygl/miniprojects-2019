package com.wootecobook.turkey.comment.domain.exception;

public class CommentUpdateFailException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "수정할 수 없습니다.";

    public CommentUpdateFailException() {
        super(DEFAULT_MESSAGE);
    }
}

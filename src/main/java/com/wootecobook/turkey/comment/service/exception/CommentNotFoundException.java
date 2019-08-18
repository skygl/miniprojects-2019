package com.wootecobook.turkey.comment.service.exception;

public class CommentNotFoundException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "해당 댓글을 찾을 수 없습니다.";

    public CommentNotFoundException(final Long id) {
        super(DEFAULT_MESSAGE + id);
    }
}

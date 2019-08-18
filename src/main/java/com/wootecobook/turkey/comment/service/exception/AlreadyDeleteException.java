package com.wootecobook.turkey.comment.service.exception;

public class AlreadyDeleteException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "해당 댓글은 이미 삭제되었습니다.";

    public AlreadyDeleteException(final Long id) {
        super(DEFAULT_MESSAGE + id);
    }
}

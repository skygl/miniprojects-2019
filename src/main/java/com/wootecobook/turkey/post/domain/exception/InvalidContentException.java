package com.wootecobook.turkey.post.domain.exception;

public class InvalidContentException extends RuntimeException {

    private static final String EMPTY_CONTENTS_ERROR_MESSAGE = "내용이 없습니다.";

    public InvalidContentException() {
        super(EMPTY_CONTENTS_ERROR_MESSAGE);
    }

}

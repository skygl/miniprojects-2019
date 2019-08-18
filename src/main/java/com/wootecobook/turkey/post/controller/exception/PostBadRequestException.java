package com.wootecobook.turkey.post.controller.exception;

public class PostBadRequestException extends RuntimeException {

    private static final String NOT_EXIST_CONTENTS_ERROR_MESSAGE = "글이 비어있으면 안됩니다.";

    public PostBadRequestException() {
        super(NOT_EXIST_CONTENTS_ERROR_MESSAGE);
    }

}

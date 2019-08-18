package com.wootecobook.turkey.post.domain.exception;

public class PostUpdateFailException extends RuntimeException {

    public static final String POST_UPDATE_ERROR_MESSAGE = "수정할 수 없습니다.";

    public PostUpdateFailException() {
        super(POST_UPDATE_ERROR_MESSAGE);
    }

}

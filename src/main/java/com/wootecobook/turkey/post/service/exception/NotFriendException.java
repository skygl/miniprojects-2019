package com.wootecobook.turkey.post.service.exception;

public class NotFriendException extends RuntimeException {

    public static final String NOT_FRIEND_MESSAGE = "태그한 유저가 친구가 아닙니다.";

    public NotFriendException() {
        super(NOT_FRIEND_MESSAGE);
    }
}
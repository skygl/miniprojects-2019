package com.wootecobook.turkey.comment.domain;

import com.wootecobook.turkey.comment.domain.exception.InvalidCommentException;

public class CommentValidator {

    public static final String INVALID_CONTENTS_MESSAGE = "내용 입력은 빈칸이 안됩니다.";

    public static void validateContents(String contents) {
        if (contents == null || contents.trim().equals("")) {
            throw new InvalidCommentException(INVALID_CONTENTS_MESSAGE);
        }
    }
}

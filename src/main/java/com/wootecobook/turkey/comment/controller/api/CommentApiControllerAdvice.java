package com.wootecobook.turkey.comment.controller.api;

import com.wootecobook.turkey.comment.domain.exception.CommentUpdateFailException;
import com.wootecobook.turkey.comment.domain.exception.InvalidCommentException;
import com.wootecobook.turkey.comment.domain.exception.NotCommentOwnerException;
import com.wootecobook.turkey.comment.service.exception.AlreadyDeleteException;
import com.wootecobook.turkey.comment.service.exception.CommentSaveException;
import com.wootecobook.turkey.commons.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.ContextNotEmptyException;

@RestControllerAdvice(basePackageClasses = {CommentApiController.class})
public class CommentApiControllerAdvice {

    @ExceptionHandler({ContextNotEmptyException.class, CommentSaveException.class, InvalidCommentException.class,
            AlreadyDeleteException.class, NotCommentOwnerException.class, CommentUpdateFailException.class})
    private ResponseEntity<ErrorMessage> handleException(Exception exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());
        return ResponseEntity.badRequest().body(errorMessage);
    }
}

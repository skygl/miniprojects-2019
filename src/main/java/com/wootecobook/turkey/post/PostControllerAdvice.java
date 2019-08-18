package com.wootecobook.turkey.post;

import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.post.controller.PostApiController;
import com.wootecobook.turkey.post.controller.exception.PostBadRequestException;
import com.wootecobook.turkey.post.service.exception.NotFoundPostException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {PostApiController.class})
public class PostControllerAdvice {

    @ExceptionHandler({PostBadRequestException.class, NotFoundPostException.class})
    public ResponseEntity<ErrorMessage> handleException(RuntimeException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());

        return ResponseEntity.badRequest().body(errorMessage);
    }

}

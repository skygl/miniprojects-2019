package com.wootecobook.turkey.user.controller.api;

import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.commons.exception.NotLoginException;
import com.wootecobook.turkey.user.service.exception.NotFoundUserException;
import com.wootecobook.turkey.user.service.exception.SignUpException;
import com.wootecobook.turkey.user.service.exception.UserDeleteException;
import com.wootecobook.turkey.user.service.exception.UserMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = UserApiController.class)
public class UserApiControllerAdvice {

    @ExceptionHandler({SignUpException.class, UserDeleteException.class, UserMismatchException.class,
            NotFoundUserException.class, NotLoginException.class})
    private ResponseEntity<ErrorMessage> handleException(Exception exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());
        return ResponseEntity.badRequest().body(errorMessage);
    }

}

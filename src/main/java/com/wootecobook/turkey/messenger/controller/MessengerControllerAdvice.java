package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.messenger.service.exception.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackageClasses = {MessengerController.class})
public class MessengerControllerAdvice {

    @ExceptionHandler({AccessDeniedException.class})
    protected ResponseEntity<ErrorMessage> handleException(Exception exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorMessage);
    }
}

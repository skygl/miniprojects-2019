package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.commons.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice(basePackageClasses = {MessengerApiController.class})
public class MessengerApiControllerAdvice {

    @ExceptionHandler({EntityNotFoundException.class})
    protected ResponseEntity<ErrorMessage> handleException(Exception exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());

        return ResponseEntity.badRequest().body(errorMessage);
    }
}

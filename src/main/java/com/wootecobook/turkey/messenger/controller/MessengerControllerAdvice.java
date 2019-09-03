package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.messenger.service.exception.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackageClasses = {MessengerController.class})
public class MessengerControllerAdvice {

    @ExceptionHandler({AccessDeniedException.class})
    protected ModelAndView handleException(Exception exception) {
        ModelAndView model = new ModelAndView("403");
        model.setStatus(HttpStatus.FORBIDDEN);
        return model;
    }
}

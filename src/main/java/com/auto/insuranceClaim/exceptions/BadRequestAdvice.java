package com.auto.insuranceClaim.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BadRequestAdvice {

    @ResponseBody
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage badRequestHandler(BadRequestException ex) {
        return new ErrorMessage(ex.getMessage());
    }
}

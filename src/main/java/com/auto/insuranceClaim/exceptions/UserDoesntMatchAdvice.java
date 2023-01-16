package com.auto.insuranceClaim.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UserDoesntMatchAdvice {

    @ResponseBody
    @ExceptionHandler(UserDoesntMatchException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorMessage  userDoesntMatchHandler(UserDoesntMatchException ex) {
        return new ErrorMessage(ex.getMessage());
    }
}

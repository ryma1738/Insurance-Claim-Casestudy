package com.auto.insuranceClaim.exceptions;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HttpMessageNotReadableAdvice {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return new ResponseEntity(new ErrorMessage(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}

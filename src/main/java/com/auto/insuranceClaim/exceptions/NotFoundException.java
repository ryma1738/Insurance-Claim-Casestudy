package com.auto.insuranceClaim.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String s) {super(s + " was not found!");}
}

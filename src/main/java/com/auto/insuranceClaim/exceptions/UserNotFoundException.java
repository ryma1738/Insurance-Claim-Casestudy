package com.auto.insuranceClaim.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() { super("User was not found;");}
}

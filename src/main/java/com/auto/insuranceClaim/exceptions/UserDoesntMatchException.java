package com.auto.insuranceClaim.exceptions;

public class UserDoesntMatchException extends RuntimeException {
    public UserDoesntMatchException() {super("User does not own this claim. You can not view claims that to not belong to you!");}
}

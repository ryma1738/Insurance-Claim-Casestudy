package com.auto.insuranceClaim.exceptions;

public class VehicleNotFoundException extends RuntimeException{
    public VehicleNotFoundException() {super("Vehicle was not found!");}
}

package com.auto.insuranceClaim.Json;

import com.auto.insuranceClaim.vehicle.Make;
import com.auto.insuranceClaim.vehicle.UseCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfoJson {
    private Make make;

    private String model;

    private int year;

    private String vin;

    private UseCase useCase;
}

package com.auto.insuranceClaim.Json;

import com.auto.insuranceClaim.claim.InsuranceClaim;
import com.auto.insuranceClaim.vehicle.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataJson {

    private String email;

    private String phoneNumber;

    private Date dob;

    private Set<InsuranceClaim> claims;

    private Set<Vehicle> vehicles;

}

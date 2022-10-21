package com.auto.insuranceClaim.Json;

import com.auto.insuranceClaim.claim.InsuranceClaim;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataJson {

    private Long id;

    private String email;

    private String phoneNumber;

    private Date dob;

    private List<InsuranceClaimFullJson> claims;

    private List<VehicleInfoJson> vehicles;

}

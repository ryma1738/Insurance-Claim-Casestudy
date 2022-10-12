package com.auto.insuranceClaim.Json;

import com.auto.insuranceClaim.claim.ClaimStatus;
import com.auto.insuranceClaim.vehicle.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceClaimJson {

    private Long Id;

    private Long userId;

    private List<DBFileJson> documents;

    private Long vehicleId;

    private String description;

    private ClaimStatus claimStatus;
}

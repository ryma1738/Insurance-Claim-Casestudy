package com.auto.insuranceClaim.Json;

import com.auto.insuranceClaim.claim.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceClaimInfoJson {

    private Long Id;

    private Long userId;

    private Long vehicleId;

    private String description;

    private ClaimStatus claimStatus;
}

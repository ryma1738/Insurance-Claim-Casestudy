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
public class InsuranceClaimJson {

    private Long Id;

    private Long userId;

    private List<DBFileJson> documents;

    private Long vehicleId;

    private String description;

    private ClaimStatus claimStatus;

    private Timestamp createdAt;
}

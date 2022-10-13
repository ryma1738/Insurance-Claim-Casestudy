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
public class InsuranceClaimFullJson {
    private Long Id;

    private UserDataBasicJson user;

    private List<DBFileJson> documents;

    private VehicleInfoJson vehicle;

    private String description;

    private ClaimStatus claimStatus;

    private Timestamp createdAt;
}

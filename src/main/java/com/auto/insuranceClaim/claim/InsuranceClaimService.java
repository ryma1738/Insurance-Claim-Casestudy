package com.auto.insuranceClaim.claim;

import com.auto.insuranceClaim.Json.DBFileJson;
import com.auto.insuranceClaim.Json.InsuranceClaimJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsuranceClaimService {
    @Autowired private InsuranceClaimRepository claimRep;

    public List<InsuranceClaimJson> getClaimsByStatus(ClaimStatus status) {
        return claimRep.findByStatus(status).stream().map(claim -> new InsuranceClaimJson(
                claim.getId(),
                claim.getUser().getId(),
                claim.getDocuments().stream()
                    .map(file -> new DBFileJson(file.getId(),
                            file.getFileName(),
                            file.getFileType())
                    ).collect(Collectors.toList()),
                claim.getVehicle().getId(),
                claim.getDescription(),
                claim.getClaimStatus())).collect(Collectors.toList());
    }
}

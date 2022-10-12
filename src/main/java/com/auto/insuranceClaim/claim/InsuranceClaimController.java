package com.auto.insuranceClaim.claim;

import com.auto.insuranceClaim.Json.InsuranceClaimJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class InsuranceClaimController {
    @Autowired
    private InsuranceClaimService claimService;

    @GetMapping("/employee/claims/{status}")
    public List<InsuranceClaimJson> getClaimsByStatus(@PathVariable ClaimStatus status) {
        return claimService.getClaimsByStatus(status);
    }
}

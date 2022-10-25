package com.auto.insuranceClaim.claim;

import com.auto.insuranceClaim.Json.InsuranceClaimCreationJson;
import com.auto.insuranceClaim.Json.InsuranceClaimFullJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class InsuranceClaimController {
    @Autowired
    private InsuranceClaimService claimService;

    @GetMapping("/employee/claims/{status}")
    public List<InsuranceClaimFullJson> getClaimsByStatus(@PathVariable ClaimStatus status) {
        return claimService.getClaimsByStatus(status);
    }
    @GetMapping("/employee/claims/")
    public List<InsuranceClaimFullJson> getClaims() {
        return claimService.getClaims();
    }

    @GetMapping("/employee/claim/{id}")
    public InsuranceClaimFullJson getClaimById(@PathVariable Long id) {
        return claimService.getClaimById(id);
    }

    @PutMapping("/employee/claim/{id}/{status}")
    public ResponseEntity<Object> updateClaimStatus(@PathVariable Long id,
                                                    @PathVariable ClaimStatus status) {
        return claimService.updateClaimStatus(id, status);
    }

    @GetMapping("/claim/{id}")
    public InsuranceClaimFullJson getClaimByIdUser(@PathVariable Long id) {
        return claimService.getClaimByIdUser(id);
    }

    @PostMapping("/claim")
    public ResponseEntity<Object> createClaim(@RequestBody InsuranceClaimCreationJson claim) {
        return claimService.createClaim(claim);
    }
}

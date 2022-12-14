package com.auto.insuranceClaim.claim;

import com.auto.insuranceClaim.user.User;
import com.auto.insuranceClaim.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {
    List<InsuranceClaim> findByUser(User user);
    List<InsuranceClaim> findByClaimStatus(ClaimStatus claimStatus);
    List<InsuranceClaim> findByVehicle(Vehicle vehicle);
}

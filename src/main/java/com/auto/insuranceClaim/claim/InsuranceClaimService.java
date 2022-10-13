package com.auto.insuranceClaim.claim;

import com.auto.insuranceClaim.Json.*;
import com.auto.insuranceClaim.exceptions.NotFoundException;
import com.auto.insuranceClaim.exceptions.UserDoesntMatchException;
import com.auto.insuranceClaim.user.User;
import com.auto.insuranceClaim.user.UserRepository;
import com.auto.insuranceClaim.vehicle.Vehicle;
import com.auto.insuranceClaim.vehicle.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InsuranceClaimService {
    @Autowired private InsuranceClaimRepository claimRep;
    @Autowired private UserRepository userRep;
    @Autowired private VehicleRepository vehicleRepository;

    public List<InsuranceClaimJson> getClaimsByStatus(ClaimStatus status) {
        return claimRep.findByClaimStatus(status).stream().map(claim -> new InsuranceClaimJson(
                claim.getId(),
                claim.getUser().getId(),
                claim.getDocuments().stream()
                    .map(file -> new DBFileJson(file.getId(),
                            file.getFileName(),
                            file.getFileType())
                    ).collect(Collectors.toList()),
                claim.getVehicle().getId(),
                claim.getDescription(),
                claim.getClaimStatus(),
                claim.getCreatedAt())).collect(Collectors.toList());
    }

    @Transactional
    public InsuranceClaimFullJson getClaimById(Long id) {
        Optional<InsuranceClaim> confirm = claimRep.findById(id);
        if (confirm.isPresent()) {
            InsuranceClaim claim = confirm.get();
            User user = claim.getUser();
            Vehicle vehicle = claim.getVehicle();
            return new InsuranceClaimFullJson(claim.getId(),
                    new UserDataBasicJson(user.getId(),
                            user.getEmail(),
                            user.getPhoneNumber(),
                            user.getDob()),
                    claim.getDocuments().stream().map(doc -> new DBFileJson(
                            doc.getId(), doc.getFileName(), doc.getFileType()
                    )).collect(Collectors.toList()),
                    new VehicleInfoJson(vehicle.getMake(),
                            vehicle.getModel(),
                            vehicle.getYear(),
                            vehicle.getVin(),
                            vehicle.getUseCase()),
                    claim.getDescription(),
                    claim.getClaimStatus(),
                    claim.getCreatedAt());
        } else throw new NotFoundException("Insurance claim");
    }

    public InsuranceClaimFullJson getClaimByIdUser(Long id) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> confirm = userRep.findByEmail(email);
        if(confirm.isPresent()){
            InsuranceClaimFullJson claim = getClaimById(id);
            if (claim.getUser().getId().equals(confirm.get().getId())) {
                return claim;
            } else throw new UserDoesntMatchException();
        } else throw new NotFoundException("User");
    }
    public ResponseEntity<Object> updateClaimStatus(Long id, ClaimStatus status) {
        Optional<InsuranceClaim> getClaim = claimRep.findById(id);
        if (getClaim.isPresent()) {
            InsuranceClaim claim = getClaim.get();
            claim.setClaimStatus(status);
            InsuranceClaim savedClaim = claimRep.save(claim);

            return ResponseEntity.ok(new InsuranceClaimCreationJson(savedClaim.getId(),
                    savedClaim.getUser().getId(),
                    savedClaim.getVehicle().getId(),
                    savedClaim.getDescription(),
                    savedClaim.getClaimStatus()));
        } else throw new NotFoundException("Insurance claim");
    }

    public ResponseEntity<Object> createClaim(InsuranceClaimCreationJson claim) {
        InsuranceClaim createdClaim = new InsuranceClaim();
        Optional<User> userConfirm = userRep.findById(claim.getUserId());
        if(userConfirm.isPresent()){
            Optional<Vehicle> vehicleConfirm = vehicleRepository.findById(claim.getVehicleId());
            if (vehicleConfirm.isPresent()) {
                createdClaim.setUser(userConfirm.get());
                createdClaim.setVehicle(vehicleConfirm.get());
                createdClaim.setDescription(claim.getDescription());
                createdClaim.setClaimStatus(ClaimStatus.PROCESSING);

                InsuranceClaim savedClaim = claimRep.save(createdClaim);
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/claim")
                        .buildAndExpand(savedClaim.getId())
                        .toUri();
                return ResponseEntity.created(location)
                        .body(new InsuranceClaimCreationJson(savedClaim.getId(),
                                savedClaim.getUser().getId(),
                                savedClaim.getVehicle().getId(),
                                savedClaim.getDescription(),
                                savedClaim.getClaimStatus()
                        ));
            } else throw new NotFoundException("Vehicle");
        } else throw new NotFoundException("User");
    }
}

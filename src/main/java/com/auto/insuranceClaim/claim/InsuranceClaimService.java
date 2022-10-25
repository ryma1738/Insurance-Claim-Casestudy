package com.auto.insuranceClaim.claim;

import com.auto.insuranceClaim.Json.*;
import com.auto.insuranceClaim.exceptions.NotFoundException;
import com.auto.insuranceClaim.exceptions.UserDoesntMatchException;
import com.auto.insuranceClaim.user.User;
import com.auto.insuranceClaim.user.UserRepository;
import com.auto.insuranceClaim.vehicle.Vehicle;
import com.auto.insuranceClaim.vehicle.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(InsuranceClaimService.class);
    @Autowired private InsuranceClaimRepository claimRep;
    @Autowired private UserRepository userRep;
    @Autowired private VehicleRepository vehicleRepository;
    public List<InsuranceClaimFullJson> getClaims() {
        logger.trace("Employee searched for all claims");
        return claimRep.findAll().stream().map(claim -> {
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
                    new VehicleInfoJson(vehicle.getId(),
                            vehicle.getMake(),
                            vehicle.getModel(),
                            vehicle.getYear(),
                            vehicle.getVin(),
                            vehicle.getUseCase()),
                    claim.getDescription(),
                    claim.getClaimStatus(),
                    claim.getCreatedAt());
        }).collect(Collectors.toList());
    }

    public List<InsuranceClaimFullJson> getClaimsByStatus(ClaimStatus status) {
        logger.trace("Employee searched claims by status. STATUS: " + status);
        return claimRep.findByClaimStatus(status).stream().map(claim -> {
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
                    new VehicleInfoJson(vehicle.getId(),
                            vehicle.getMake(),
                            vehicle.getModel(),
                            vehicle.getYear(),
                            vehicle.getVin(),
                            vehicle.getUseCase()),
                    claim.getDescription(),
                    claim.getClaimStatus(),
                    claim.getCreatedAt());
        }).collect(Collectors.toList());
    }

    @Transactional
    public InsuranceClaimFullJson getClaimById(Long id) {
        logger.trace("Employee searched for claim with id: " + id);
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
                    new VehicleInfoJson(vehicle.getId(),
                            vehicle.getMake(),
                            vehicle.getModel(),
                            vehicle.getYear(),
                            vehicle.getVin(),
                            vehicle.getUseCase()),
                    claim.getDescription(),
                    claim.getClaimStatus(),
                    claim.getCreatedAt());
        } else {
            logger.error("Insurance claim with id: " + id + ", was not found");
            throw new NotFoundException("Insurance claim");
        }
    }

    public InsuranceClaimFullJson getClaimByIdUser(Long id) {
        logger.trace("User is getting claim with id: " + id);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> confirm = userRep.findByEmail(email);
        if(confirm.isPresent()){
            InsuranceClaimFullJson claim = getClaimById(id);
            if (claim.getUser().getId().equals(confirm.get().getId())) {
                return claim;
            } else {
                logger.warn("User with id: " + confirm.get().getId() + " attempted to get a claim that did not belong to them");
                throw new UserDoesntMatchException();
            }
        } else {
            logger.error("User was not found");
            throw new NotFoundException("User");
        }
    }
    public ResponseEntity<Object> updateClaimStatus(Long id, ClaimStatus status) {
        logger.trace("Employee is attempting to update the status of claim with id: " + id);
        Optional<InsuranceClaim> getClaim = claimRep.findById(id);
        if (getClaim.isPresent()) {
            InsuranceClaim claim = getClaim.get();
            claim.setClaimStatus(status);
            InsuranceClaim savedClaim = claimRep.save(claim);

            return ResponseEntity.ok(new InsuranceClaimInfoJson(savedClaim.getId(),
                    savedClaim.getUser().getId(),
                    savedClaim.getVehicle().getId(),
                    savedClaim.getDescription(),
                    savedClaim.getClaimStatus()));
        } else {
            logger.error("Insurance claim was not found");
            throw new NotFoundException("Insurance claim");
        }
    }

    public ResponseEntity<Object> createClaim(InsuranceClaimCreationJson claim) {
        logger.trace("User is attempting to create a new claim");
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
                logger.info("New Claim was created successfully");
                return ResponseEntity.created(location)
                        .body(new InsuranceClaimInfoJson(savedClaim.getId(),
                                savedClaim.getUser().getId(),
                                savedClaim.getVehicle().getId(),
                                savedClaim.getDescription(),
                                savedClaim.getClaimStatus()
                        ));
            } else {
                logger.error("Vehicle was not found for claim creation");
                throw new NotFoundException("Vehicle");
            }
        } else {
            logger.error("User was not found");
            throw new NotFoundException("User");
        }
    }
}

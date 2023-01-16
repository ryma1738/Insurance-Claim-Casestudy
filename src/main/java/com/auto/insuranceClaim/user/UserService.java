package com.auto.insuranceClaim.user;

import com.auto.insuranceClaim.Json.*;
import com.auto.insuranceClaim.claim.InsuranceClaim;
import com.auto.insuranceClaim.claim.InsuranceClaimRepository;
import com.auto.insuranceClaim.dbFile.DBFileRepository;
import com.auto.insuranceClaim.exceptions.BadRequestException;
import com.auto.insuranceClaim.exceptions.NotFoundException;
import com.auto.insuranceClaim.security.JWTUtil;
import com.auto.insuranceClaim.vehicle.Vehicle;
import com.auto.insuranceClaim.vehicle.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired private UserRepository userRep;
    @Autowired private InsuranceClaimRepository claimRep;
    @Autowired private DBFileRepository DBFileRep;
    @Autowired private VehicleRepository vehicleRep;

    @Autowired private JWTUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;
    @Autowired private PasswordEncoder passwordEncoder;

    public ResponseEntity<UserDataJson> getUserInfo() {
        logger.trace("Get user info route called ");
        User user = userRep.findByEmail((String)
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .orElseThrow(() -> {
                    logger.error("User Was not found for getUserInfo");
                    return new NotFoundException("User");
                });
        Set<InsuranceClaim> claims = user.getClaims();
        Set<Vehicle> vehicles = user.getVehicles();
        return ResponseEntity.ok(new UserDataJson(user.getId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDob(),
                claims.stream().map(claim -> new InsuranceClaimFullJson(claim.getId(),
                        new UserDataBasicJson(user.getId(),
                                user.getEmail(),
                                user.getPhoneNumber(),
                                user.getDob()),
                        claim.getDocuments().stream().map(doc -> new DBFileJson(
                                doc.getId(), doc.getFileName(), doc.getFileType()
                        )).collect(Collectors.toList()),
                        new VehicleInfoJson(claim.getVehicle().getId(),
                                claim.getVehicle().getMake(),
                                claim.getVehicle().getModel(),
                                claim.getVehicle().getYear(),
                                claim.getVehicle().getVin(),
                                claim.getVehicle().getUseCase()),
                        claim.getDescription(),
                        claim.getClaimStatus(),
                        claim.getCreatedAt())).collect(Collectors.toList()),
                vehicles.stream().map(vehicle -> new VehicleInfoJson(vehicle.getId(),
                        vehicle.getMake(),
                        vehicle.getModel(),
                        vehicle.getYear(),
                        vehicle.getVin(),
                        vehicle.getUseCase())).collect(Collectors.toList())
        ));
    }

    public Map<String, String> createUser(SignUpCredentials signupInfo) {
        logger.trace("Create User Called");
        User user = new User();
        String encodedPass = passwordEncoder.encode(signupInfo.getPassword());
        user.setPassword(encodedPass);
        user.setEmail(signupInfo.getEmail());
        user.setPhoneNumber(signupInfo.getPhoneNumber());
        user.setDob(signupInfo.getDob());
        userRep.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        return Collections.singletonMap("jwtToken", token);
    }

    public Map<String, String> createEmployee(SignUpCredentials signupInfo) {
        logger.trace("New Employee creation called");
        User user = new User();
        String encodedPass = passwordEncoder.encode(signupInfo.getPassword());
        user.setPassword(encodedPass);
        user.setEmail(signupInfo.getEmail());
        user.setPhoneNumber(signupInfo.getPhoneNumber());
        user.setDob(signupInfo.getDob());
        user.setRole("employee");
        userRep.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        return Collections.singletonMap("jwtToken", token);
    }

    public Map<String, String> loginUser(LoginCredentials body) {
        logger.trace("User Attempted Login");
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
            authManager.authenticate(authInputToken);
            String token = jwtUtil.generateToken(body.getEmail());
            logger.trace("User Logged in successfully");
            return Collections.singletonMap("jwtToken", token);
        } catch (AuthenticationException authExc){
            logger.error("User Login had Invalid Login Credentials");
            throw new BadRequestException("Invalid Login Credentials");
        }
    }

    public List<UserDataBasicJson> getAllUsers() {
        logger.trace("Search for all users Called");
        return userRep.findAllByRole("user").stream()
                .map(user -> {
                    return new UserDataBasicJson(user.getId(), user.getEmail(), user.getPhoneNumber(),
                            user.getDob());
                }).collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<Object> addVehicle(VehicleInfoJson vehicleInfo) {
        logger.trace("User is attempting to add vehicle to their account");
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> getUser = userRep.findByEmail(email);
        if (getUser.isPresent()) {
            try {
                User user = getUser.get();
                Vehicle vehicle = new Vehicle();
                vehicle.setUser(user);
                vehicle.setMake(vehicleInfo.getMake());
                vehicle.setModel(vehicleInfo.getModel());
                vehicle.setYear(vehicleInfo.getYear());
                vehicle.setVin(vehicleInfo.getVin());
                vehicle.setUseCase(vehicleInfo.getUseCase());
                Vehicle savedVehicle = vehicleRep.save(vehicle);

                Set<Vehicle> vehicleSet = user.getVehicles();
                vehicleSet.add(savedVehicle);
                user.setVehicles(vehicleSet);
                userRep.save(user);

                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/user/vehicle")
                        .buildAndExpand(user.getId())
                        .toUri();
                logger.info("User added new vehicle successfully");
                return ResponseEntity.created(location).build();
            } catch (Exception ex) {
                logger.error("Invalid Format for Vehicle Registration: ERROR: " + ex.getLocalizedMessage());
                throw new BadRequestException("Invalid Format for Vehicle Registration: ERROR: " + ex.getLocalizedMessage());
            }
        } else {
            logger.error("User was not found!");
            throw new NotFoundException("User");
        }
    }

    @Transactional
    public ResponseEntity<Object> deleteVehicle(Long vehicleId) {
        logger.trace("User is attempting to delete vehicle");
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> getUser = userRep.findByEmail(email);
        if (getUser.isPresent()) {
            User user = getUser.get();
            Set<Vehicle> vehicles = user.getVehicles();
            Optional<Vehicle> getVehicle = vehicleRep.findById(vehicleId);
            if (getVehicle.isPresent() || getVehicle.get().getUser().equals(null)) {
                System.out.println(vehicles);
                System.out.println(getVehicle.get());
                vehicles.remove(getVehicle.get());
                user.setVehicles(vehicles);
                userRep.save(user);
                Vehicle vehicle = getVehicle.get();
                if (claimRep.findByVehicle(vehicle).isEmpty()) {
                    vehicleRep.delete(vehicle);
                    logger.info("Vehicle with id: " + vehicle.getId() + ", has been deleted.");
                } else {
                    vehicle.setUser(null);
                    vehicleRep.save(vehicle);
                    logger.info("Vehicle with id: " + vehicle.getId() + ", has been removed from users account.");
                }
                return ResponseEntity.ok().build();
            } else {
                logger.error("Vehicle was not found");
                throw new NotFoundException("Vehicle");
            }
        } else {
            logger.error("User was not found");
            throw new NotFoundException("User");
        }
    }

    public ResponseEntity<Object> getRole() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> getUser = userRep.findByEmail(email);
        if (getUser.isPresent()) {
            logger.trace("User role checked");
            return ResponseEntity.ok(new RoleJson(getUser.get().getRole()));
        } else {
            logger.error("User not found");
            throw new NotFoundException("User");
        }
    }

    @Transactional
    public ResponseEntity<Object> deleteUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> getUser = userRep.findByEmail(email);
        if (getUser.isPresent()) {
            User user = getUser.get();
            vehicleRep.findByUser(user).forEach(vehicle -> {
                logger.info("Vehicle with id: " + vehicle.getId() + ", was deleted from the system");
                vehicleRep.delete(vehicle);
            });
            claimRep.findByUser(user).forEach(claim -> {
                claim.getDocuments().forEach(dbFile -> DBFileRep.delete(dbFile));
                vehicleRep.delete(claim.getVehicle());
                logger.info("Vehicle with id: " + claim.getVehicle().getId() + ", was deleted from the system");
                claimRep.delete(claim);
                logger.info("Claim with id: " + claim.getId() + ", was deleted from the system");
            });
            userRep.delete(user);
            logger.info("User with id: " + user.getId() + ", was deleted from the system.");
            return ResponseEntity.ok().build();
        } else {
            logger.error("User not found");
            throw new NotFoundException("User");
        }
    }

}
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
    @Autowired private UserRepository userRep;
    @Autowired private InsuranceClaimRepository claimRep;
    @Autowired private DBFileRepository DBFileRep;

    @Autowired private VehicleRepository vehicleRep;

    @Autowired private JWTUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;
    @Autowired private PasswordEncoder passwordEncoder;

    public ResponseEntity<UserDataJson> getUserInfo() {
        User user = userRep.findByEmail((String)
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .orElseThrow(() -> new NotFoundException("User"));
        Set<InsuranceClaim> claims = user.getClaims();
        Set<Vehicle> vehicles = user.getVehicles();
        return ResponseEntity.ok(new UserDataJson(user.getId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDob(),
                claims.stream().map(claim -> new InsuranceClaimJson(claim.getId(),
                        claim.getUser().getId(),
                        claim.getDocuments().stream()
                                .map(file -> new DBFileJson(file.getId(),
                                        file.getFileName(),
                                        file.getFileType())
                                ).collect(Collectors.toList()),
                        claim.getVehicle().getId(),
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
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
            authManager.authenticate(authInputToken);
            String token = jwtUtil.generateToken(body.getEmail());

            return Collections.singletonMap("jwtToken", token);
        } catch (AuthenticationException authExc){
            throw new BadRequestException("Invalid Login Credentials");
        }
    }

    public List<UserDataBasicJson> getAllUsers() {
        return userRep.findAllByRole("user").stream()
                .map(user -> {
                    return new UserDataBasicJson(user.getId(), user.getEmail(), user.getPhoneNumber(),
                            user.getDob());
                }).collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<Object> addVehicle(VehicleInfoJson vehicleInfo) {
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
                return ResponseEntity.created(location).build();
            } catch (Exception ex) {
                throw new BadRequestException("Invalid Format for Vehicle Registration: ERROR: " + ex.getLocalizedMessage());
            }
        } else throw new NotFoundException("User");
    }

    @Transactional
    public ResponseEntity<Object> deleteVehicle(Long vehicleId) {
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
                } else {
                    vehicle.setUser(null);
                    vehicleRep.save(vehicle);
                }
                return ResponseEntity.ok().build();
            } else throw new NotFoundException("Vehicle");
        } else throw new NotFoundException("User");
    }

    public ResponseEntity<Object> getRole() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> getUser = userRep.findByEmail(email);
        if (getUser.isPresent()) {
            return ResponseEntity.ok(new RoleJson(getUser.get().getRole()));
        } else throw new NotFoundException("User");
    }

    @Transactional
    public ResponseEntity<Object> deleteUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> getUser = userRep.findByEmail(email);
        if (getUser.isPresent()) {
            User user = getUser.get();
            vehicleRep.findByUser(user).forEach(vehicle -> vehicleRep.delete(vehicle));
            claimRep.findByUser(user).forEach(claim -> {
                claim.getDocuments().forEach(dbFile -> DBFileRep.delete(dbFile));
                vehicleRep.delete(claim.getVehicle());
                claimRep.delete(claim);
            });
            userRep.delete(user);
            return ResponseEntity.ok().build();
        } else throw new NotFoundException("User");
    }

}
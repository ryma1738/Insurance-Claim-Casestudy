package com.auto.insuranceClaim.user;

import com.auto.insuranceClaim.Json.LoginCredentials;
import com.auto.insuranceClaim.Json.SignUpCredentials;
import com.auto.insuranceClaim.Json.VehicleInfoJson;
import com.auto.insuranceClaim.exceptions.BadRequestException;
import com.auto.insuranceClaim.exceptions.UserNotFoundException;
import com.auto.insuranceClaim.exceptions.VehicleNotFoundException;
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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @Autowired private UserRepository userRep;

    @Autowired private VehicleRepository vehicleRep;

    @Autowired private JWTUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;
    @Autowired private PasswordEncoder passwordEncoder;

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
        } else throw new UserNotFoundException();
    }

    @Transactional
    public ResponseEntity<Object> deleteVehicle(Long vehicleId) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> getUser = userRep.findByEmail(email);
        if (getUser.isPresent()) {
            User user = getUser.get();
            Set<Vehicle> vehicles = user.getVehicles();
            Optional<Vehicle> getVehicle = vehicleRep.findById(vehicleId);
            if (getVehicle.isPresent()) {
                vehicles.remove(getVehicle.get());
                user.setVehicles(vehicles);
                userRep.save(user);
                vehicleRep.delete(getVehicle.get());
                return ResponseEntity.ok().build();
            } else throw new VehicleNotFoundException();
        } else throw new UserNotFoundException();
    }
}
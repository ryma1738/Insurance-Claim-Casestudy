package com.auto.insuranceClaim.user;

import com.auto.insuranceClaim.Json.LoginCredentials;
import com.auto.insuranceClaim.Json.SignUpCredentials;
import com.auto.insuranceClaim.Json.VehicleInfoJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/user/create")
    public Map<String, String> createUser(@RequestBody SignUpCredentials info) {
        return userService.createUser(info);
    }

    @PostMapping("/admin/user/create")
    public Map<String, String> createEmployee(@RequestBody SignUpCredentials info) {
        return userService.createEmployee(info);
    }

    @PostMapping("/user/login")
    public Map<String, String> login(@RequestBody LoginCredentials info) {
        return userService.loginUser(info);
    }

    @PutMapping("/user/vehicle")
    public ResponseEntity<Object> addVehicle(@RequestBody VehicleInfoJson vehicle) {
        return userService.addVehicle(vehicle);
    }

    @DeleteMapping("/user/vehicle/{vehicleId}")
    public ResponseEntity<Object> deleteVehicle(@RequestParam Long vehicleId) {
        return userService.deleteVehicle(vehicleId);
    }
}

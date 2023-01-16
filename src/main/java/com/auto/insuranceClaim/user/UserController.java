package com.auto.insuranceClaim.user;

import com.auto.insuranceClaim.Json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user/role")
    public ResponseEntity<Object> getRole() {
        return userService.getRole();
    }

    @GetMapping("/user")
    public ResponseEntity<UserDataJson> getUserInfo() {
        return userService.getUserInfo();
    }

    @GetMapping("/employee/users")
    public List<UserDataBasicJson> getAllUsers() {
        return userService.getAllUsers();
    }

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
    public ResponseEntity<Object> deleteVehicle(@PathVariable Long vehicleId) {
        return userService.deleteVehicle(vehicleId);
    }

    @DeleteMapping("/user")
    public ResponseEntity<Object> deleteUser() {
        return userService.deleteUser();
    }
}

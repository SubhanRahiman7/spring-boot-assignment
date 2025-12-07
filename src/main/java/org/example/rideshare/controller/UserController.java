package org.example.rideshare.controller;

import org.example.rideshare.dto.RideResponse;
import org.example.rideshare.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final RideService rideService;

    public UserController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/rides")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RideResponse>> getUserRides(Authentication authentication) {
        String userId = extractUserIdentifier(authentication);
        List<RideResponse> rides = rideService.getUserRides(userId);
        return ResponseEntity.ok(rides);
    }

    private String extractUserIdentifier(Authentication authentication) {
        return authentication.getName();
    }
}


package org.example.rideshare.controller;

import org.example.rideshare.dto.RideResponse;
import org.example.rideshare.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver")
public class DriverController {

    private final RideService rideService;

    public DriverController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/rides/requests")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<RideResponse>> getPendingRideRequests() {
        List<RideResponse> requests = rideService.getPendingRideRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/rides/{rideId}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponse> acceptRide(
            @PathVariable String rideId,
            Authentication authentication) {
        String driverId = getDriverIdFromAuth(authentication);
        RideResponse response = rideService.acceptRide(rideId, driverId);
        return ResponseEntity.ok(response);
    }

    private String getDriverIdFromAuth(Authentication authentication) {
        return authentication.getName();
    }
}


package org.example.rideshare.service;

import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.dto.RideResponse;
import org.example.rideshare.exception.BadRequestException;
import org.example.rideshare.exception.NotFoundException;
import org.example.rideshare.model.Ride;
import org.example.rideshare.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class RideService {

    private static final String STATUS_REQUESTED = "REQUESTED";
    private static final String STATUS_ACCEPTED = "ACCEPTED";
    private static final String STATUS_COMPLETED = "COMPLETED";

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public RideResponse createRide(CreateRideRequest request, String userId) {
        Ride newRide = initializeRide(request, userId);
        Ride persistedRide = rideRepository.save(newRide);
        return mapRideToResponse(persistedRide);
    }

    public List<RideResponse> getUserRides(String userId) {
        return rideRepository.findByUserId(userId)
                .stream()
                .map(this::mapRideToResponse)
                .toList();
    }

    public List<RideResponse> getPendingRideRequests() {
        return rideRepository.findByStatus(STATUS_REQUESTED)
                .stream()
                .map(this::mapRideToResponse)
                .toList();
    }

    public RideResponse acceptRide(String rideId, String driverId) {
        Ride ride = findRideById(rideId);
        validateRideStatus(ride, STATUS_REQUESTED, "Ride is not in REQUESTED status");
        
        updateRideForAcceptance(ride, driverId);
        Ride updatedRide = rideRepository.save(ride);
        return mapRideToResponse(updatedRide);
    }

    public RideResponse completeRide(String rideId) {
        Ride ride = findRideById(rideId);
        validateRideStatus(ride, STATUS_ACCEPTED, "Ride must be ACCEPTED before completion");
        
        ride.setStatus(STATUS_COMPLETED);
        Ride updatedRide = rideRepository.save(ride);
        return mapRideToResponse(updatedRide);
    }

    private Ride initializeRide(CreateRideRequest request, String userId) {
        Ride ride = new Ride();
        ride.setUserId(userId);
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDropLocation(request.getDropLocation());
        ride.setStatus(STATUS_REQUESTED);
        ride.setCreatedAt(Date.from(Instant.now()));
        return ride;
    }

    private Ride findRideById(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));
    }

    private void validateRideStatus(Ride ride, String expectedStatus, String errorMessage) {
        if (!expectedStatus.equals(ride.getStatus())) {
            throw new BadRequestException(errorMessage);
        }
    }

    private void updateRideForAcceptance(Ride ride, String driverId) {
        ride.setDriverId(driverId);
        ride.setStatus(STATUS_ACCEPTED);
    }

    private RideResponse mapRideToResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getUserId(),
                ride.getDriverId(),
                ride.getPickupLocation(),
                ride.getDropLocation(),
                ride.getStatus(),
                ride.getCreatedAt()
        );
    }
}


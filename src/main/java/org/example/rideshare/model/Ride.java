package org.example.rideshare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {
    @Id
    private String id;
    private String userId; // Passenger
    private String driverId; // Driver (nullable)
    private String pickupLocation;
    private String dropLocation;
    private String status; // REQUESTED, ACCEPTED, COMPLETED
    private Date createdAt;
}


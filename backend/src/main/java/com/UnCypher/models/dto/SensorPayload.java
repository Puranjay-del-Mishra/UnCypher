package com.UnCypher.models.dto;
import lombok.Data;

@Data
public class SensorPayload {
    // Biometric
    private Double heartRate; // bpm
    private Double stressLevel; // 0.0 - 1.0
    private Double sleepScore;

    // Vehicle
    private Boolean isInVehicle;
    private String vehicleType; // e.g., "EV", "Bike", "Car"
    private Double fuelLevel;   // % for EV or gas tank
    private Boolean isDriving;

    // Environment
    private Double ambientNoiseDb;
    private Double temperatureCelsius;
}

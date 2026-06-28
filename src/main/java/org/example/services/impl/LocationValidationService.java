package org.example.services.impl;

import org.example.models.Coordinates;
import org.springframework.stereotype.Service;

@Service
public class LocationValidationService {
    private static final double HQ_LAT = 51.2511;
    private static final double HQ_LON = 22.5750;

    private static final double ALLOWED_RADIUS_METERS = 500.0;
    private static final int EARTH_RADIUS_KM = 6371;

    public boolean isVehicleInAllowedZone(Coordinates coordinates) {
        if (coordinates == null || coordinates.getLatitude() == null || coordinates.getLongitude() == null) {
            return false;
        }
        double distance = calculateHaversineDistance(coordinates.getLatitude(), coordinates.getLongitude());

        return (distance * 1000) <= ALLOWED_RADIUS_METERS;
    }

    private double calculateHaversineDistance(double lat1, double lon1) {
        double dLat = Math.toRadians(LocationValidationService.HQ_LAT - lat1);
        double dLon = Math.toRadians(LocationValidationService.HQ_LON - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(LocationValidationService.HQ_LAT)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}

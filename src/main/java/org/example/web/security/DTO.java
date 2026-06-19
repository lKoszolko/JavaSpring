package org.example.web.security;

import org.example.models.Address;

public class DTO {
    public record LoginRequest(
            String login,
            String password
    ) { }
    public record RegisterRequest(
            String login,
            String password,
            String matchingPassword,
            Address address
    ) { }
    public record LoginResponse(
            String token
    ) { }
    public record RentalRequest(
            String vehicleId
    ) { }
}

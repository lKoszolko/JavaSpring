package org.example.web.security;

public class DTO {
    public record LoginRequest(
            String login,
            String password
    ) { }
    public record RegisterRequest(
            String login,
            String password,
            String matchingPassword
    ) { }
    public record LoginResponse(
            String token
    ) { }
    public record RentalRequest(
            String vehicleId
    ) { }
}

package org.example.web;

import org.example.models.Rental;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.example.services.servicesInterfaces.RentalServiceInterface;
import org.example.web.security.DTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalServiceInterface rentalService;
    private final UserRepository userRepository;

    public RentalController(RentalServiceInterface rentalService, UserRepository userRepository) {
        this.rentalService = rentalService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Rental> list() {
        return rentalService.findAllRentals();
    }

    @GetMapping("/users/{userId}")
    public List<Rental> userRentals(@PathVariable String userId) {
        return rentalService.findUserRentals(userId);
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rent(@RequestBody DTO.RentalRequest rentalRequest,
                                       @AuthenticationPrincipal UserDetails userDetails) {

        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));

        Rental rental = rentalService.rentVehicle(user.getId(), rentalRequest.vehicleId());

        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(@AuthenticationPrincipal UserDetails userDetails) {

        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));

        Rental rental = rentalService.returnVehicle(user.getId());
        return ResponseEntity.ok(rental);
    }

    @PostMapping("/test-roli")
    public ResponseEntity<String> testRoli(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("Moje role to: " + userDetails.getAuthorities());
        return ResponseEntity.ok("Zalogowany jako: " + userDetails.getUsername());
    }
}
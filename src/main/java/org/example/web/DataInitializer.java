package org.example.web;

import org.example.models.User;
import org.example.models.Vehicle;
import org.example.models.Role;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           VehicleRepository vehicleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByLogin("admin").isEmpty()) {
            User admin = new User();
            admin.setId(UUID.randomUUID().toString());
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setLogin("kowalski");
            user.setPassword(passwordEncoder.encode("kowalski123"));
            user.setRole(Role.USER);
            userRepository.save(user);

            System.out.println("✅ BAZA: Wygenerowano testowych użytkowników!");
        }

        if (vehicleRepository.findAll().isEmpty()) {

            Vehicle v1 = new Vehicle();
            v1.setId(UUID.randomUUID().toString());
            v1.setBrand("Toyota");
            v1.setModel("Yaris");
            v1.setPlate("LU 12345");
            vehicleRepository.save(v1);

            Vehicle v2 = new Vehicle();
            v2.setId(UUID.randomUUID().toString());
            v2.setBrand("Ford");
            v2.setModel("Mustang");
            v2.setPlate("WA 54321");
            vehicleRepository.save(v2);

            System.out.println("✅ BAZA: Dodano testowe pojazdy na parking!");
        }
    }
}
package org.example.services;

import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.example.services.servicesInterfaces.UserServiceInterface;
import org.example.web.security.DTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepo;
    private final RentalService rentalService;

    public UserService(UserRepository userRepo, RentalService rentalService) {
        this.userRepo = userRepo;
        this.rentalService = rentalService;
    }

    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    public User findById(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o ID: " + id));
    }

    public void deleteUser(String idToDelete, String loggedInUserId) {
        if (idToDelete.equals(loggedInUserId)) {
            throw new IllegalArgumentException("Nie możesz usunąć swojego własnego konta!");
        }
        if (rentalService.findActiveRentalByUserId(idToDelete).isPresent()) {
            throw new IllegalStateException("Użytkownik posiada nieoddany pojazd. Najpierw musi go zwrócić.");
        }
        findById(idToDelete);
        userRepo.deleteById(idToDelete);
    }

    public void registerUser(DTO.RegisterRequest request){
        if (!request.password().equals(request.matchingPassword())) {
            throw new IllegalArgumentException("Podane hasła nie są takie same");
        }

        if (userRepo.findByLogin(request.login()).isPresent()) {
            throw new IllegalArgumentException("Podany login już istnieje");
        }
        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString());
        newUser.setLogin(request.login());
        newUser.setPassword(request.password());
        newUser.setRole(Role.USER);

        userRepo.save(newUser);

    }
}
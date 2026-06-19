package org.example.web;

import org.example.models.Coordinates;
import org.example.models.Vehicle;
import org.example.services.impl.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> list(@RequestParam(name = "available", required = false, defaultValue = "false") boolean available) {
        return available ? vehicleService.findAvailableVehicles() : vehicleService.findAll();
    }

    @GetMapping("/{id}")
    public Vehicle get(@PathVariable String id) {
        return vehicleService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addVehicle")
    public ResponseEntity<Vehicle> create(@RequestBody Vehicle vehicle) {
        Vehicle savedVehicle = vehicleService.addVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vehicleService.removeVehicle(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/location")
    public ResponseEntity<String> updateLocation(@PathVariable String id, @RequestBody Coordinates coordinates){
        Vehicle vehicle = vehicleService.findById(id);
        vehicle.setCurrentLocation(coordinates);

        vehicleService.addVehicle(vehicle);
        return ResponseEntity.ok("Lokalizacja pojazdu zaktualizowana");
    }

}
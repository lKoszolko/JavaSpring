package org.example.repositories.impl.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleJsonRepository implements VehicleRepository {

    private List<Vehicle> vehicles = new ArrayList<>();
    private final String FILE_PATH = "vehicles.json";
    private final Gson gson;

    public VehicleJsonRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        load();
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicles.stream().map(Vehicle::copy).toList();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(v -> v.getId().equals(id))
                .map(Vehicle::copy)
                .findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        vehicles.removeIf(v -> v.getId().equals(vehicle.getId()));

        Vehicle toSave = vehicle.copy();
        vehicles.add(toSave);
        saveToFile();

        return toSave;
    }

    @Override
    public void deleteById(String id) {
        if (vehicles.removeIf(v -> v.getId().equals(id))) {
            saveToFile();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(vehicles, writer);
        } catch (IOException e) {
            System.err.println("Błąd zapisu do pliku JSON: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Vehicle>>(){}.getType();
            List<Vehicle> loadedVehicles = gson.fromJson(reader, listType);

            if (loadedVehicles != null) {
                this.vehicles = loadedVehicles;
            }
        } catch (IOException e) {
            System.err.println("Błąd odczytu z pliku JSON: " + e.getMessage());
        }
    }
}
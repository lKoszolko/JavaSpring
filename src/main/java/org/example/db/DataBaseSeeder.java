package org.example.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.CategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DataBaseSeeder implements CommandLineRunner {

    private final VehicleCategoryConfigRepository repository;
    private final ObjectMapper objectMapper;

    public DataBaseSeeder(VehicleCategoryConfigRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            System.out.println("🚀 Wykryto pustą tabelę kategorii. Rozpoczynam wczytywanie pliku JSON...");

            try (InputStream inputStream = TypeReference.class.getResourceAsStream("/categories.json")) {
                if (inputStream == null) {
                    System.out.println("❌ Błąd: Nie znaleziono pliku categories.json w folderze resources!");
                    return;
                }

                List<CategoryConfig> categories = objectMapper.readValue(inputStream, new TypeReference<>() {});

                repository.saveAll(categories);
                System.out.println("✅ Pomyślnie wczytano " + categories.size() + " kategorii z pliku JSON do bazy danych!");
            } catch (Exception e) {
                System.out.println("❌ Wystąpił błąd podczas parsowania pliku JSON: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ Tabela konfiguracji kategorii zawiera już dane. Pomijam krok Seeding danych.");
        }
    }
}
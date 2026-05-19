package org.example.services;

import org.example.models.CategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VehicleCategoryConfigService {
    private final VehicleCategoryConfigRepository configRepository;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public List<CategoryConfig> findAllCategories() {
        return configRepository.findAll();
    }

    public CategoryConfig getByCategory(String categoryName) {
        CategoryConfig config = configRepository.findByName(categoryName);
        if (config == null) {
            throw new IllegalArgumentException("Nie znaleziono konfiguracji dla kategorii: " + categoryName);
        }
        return config;
    }
}
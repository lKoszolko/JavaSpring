package org.example.services.impl;

import org.example.models.CategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class VehicleCategoryConfigService {
    private final VehicleCategoryConfigRepository configRepository;
    private final JsonSchemaValidationService schemaValidator;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository configRepository, JsonSchemaValidationService schemaValidator) {
        this.configRepository = configRepository;
        this.schemaValidator = schemaValidator;
    }

    public CategoryConfig createCategory(CategoryConfig categoryConfig) {
        Set<String> errors = schemaValidator.validateAttributes(categoryConfig.getAttributes(), "category-schema.json");
        if (!errors.isEmpty()){
            throw new IllegalArgumentException("Błędne atrybuty JSON! Powód: " + String.join(", ", errors));
        }
        return configRepository.save(categoryConfig);
    }

    public List<CategoryConfig> findAllCategories() {
        return configRepository.findAll();
    }

    public CategoryConfig getByCategory(String categoryName) {
        CategoryConfig config = configRepository.findByCategory(categoryName);
        if (config == null) {
            throw new IllegalArgumentException("Nie znaleziono konfiguracji dla kategorii: " + categoryName);
        }
        return config;
    }
}
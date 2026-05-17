package org.example.repositories;

import org.example.models.CategoryConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface VehicleCategoryConfigRepository {
    List<CategoryConfig> findAll();
    CategoryConfig findByName(String name);
}

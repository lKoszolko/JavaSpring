package org.example.repositories.impl.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.models.CategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {
    List<CategoryConfig> categories = new ArrayList<>();

    public VehicleCategoryConfigJsonRepository() {
        try (FileReader reader = new FileReader("categories 1.json")) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<CategoryConfig>>(){}.getType();
            categories = gson.fromJson(reader, listType);
        } catch (Exception e) {
            System.err.println("Błąd odczytu categories.json: " + e.getMessage());
        }
    }

    @Override
    public List<CategoryConfig> findAll() {
        return categories;
    }

    @Override
    public CategoryConfig findByName(String name) {
        return categories.stream().filter(c -> c.getCategory().
                equalsIgnoreCase(name)).
                findFirst().
                orElse(null);
    }
}

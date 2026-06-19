package org.example.web;

import org.example.models.CategoryConfig;
import org.example.services.impl.VehicleCategoryConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final VehicleCategoryConfigService categoryService;

    public CategoryController(VehicleCategoryConfigService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryConfig> list() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/{category}")
    public CategoryConfig get(@PathVariable String category) {
        return categoryService.getByCategory(category);
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryConfig categoryConfig) {
        try {
            CategoryConfig savedConfig = categoryService.createCategory(categoryConfig);
            return ResponseEntity.ok(savedConfig);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
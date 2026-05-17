package org.example.web;

import org.example.models.CategoryConfig;
import org.example.services.VehicleCategoryConfigService;
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
}
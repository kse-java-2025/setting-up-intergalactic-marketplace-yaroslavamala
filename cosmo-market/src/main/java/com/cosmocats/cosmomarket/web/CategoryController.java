package com.cosmocats.cosmomarket.web;

import com.cosmocats.cosmomarket.dto.category.CategoryCreateDto;
import com.cosmocats.cosmomarket.dto.category.CategoryReturnDto;
import com.cosmocats.cosmomarket.service.CategoryServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryServiceInterface service;

    public CategoryController(CategoryServiceInterface service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryReturnDto createCategory(@Valid @RequestBody CategoryCreateDto dto) {
        return service.createNewCategory(dto);
    }

    @GetMapping
    public List<CategoryReturnDto> getAllCategories() {
        return service.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryReturnDto getCategoryById(@PathVariable Long id) {
        return service.getCategoryById(id);
    }

    @PutMapping("/{id}")
    public CategoryReturnDto updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryCreateDto dto) {
        return service.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id);
    }
}

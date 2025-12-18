package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.dto.category.CategoryCreateDto;
import com.cosmocats.cosmomarket.dto.category.CategoryReturnDto;
import java.util.List;

public interface CategoryServiceInterface {
    CategoryReturnDto createNewCategory(CategoryCreateDto dto);
    List<CategoryReturnDto> getAllCategories();
    CategoryReturnDto getCategoryById(Long id);
    CategoryReturnDto updateCategory(Long id, CategoryCreateDto dto);
    void deleteCategory(Long id);
}

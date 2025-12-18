package com.cosmocats.cosmomarket.service.impl;

import com.cosmocats.cosmomarket.dto.category.CategoryCreateDto;
import com.cosmocats.cosmomarket.dto.category.CategoryReturnDto;
import com.cosmocats.cosmomarket.exception.CategoryNotFoundException;
import com.cosmocats.cosmomarket.repository.CategoryRepository;
import com.cosmocats.cosmomarket.repository.entity.CategoryEntity;
import com.cosmocats.cosmomarket.service.CategoryServiceInterface;
import com.cosmocats.cosmomarket.service.mapper.CategoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryServiceInterface {

    private final CategoryRepository categoryRepo;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryReturnDto createNewCategory(CategoryCreateDto dto) {
        CategoryEntity toSaveCategory = categoryMapper.buildCategory(dto);
        CategoryEntity savedCategory = categoryRepo.save(toSaveCategory);
        return categoryMapper.buildCategoryReturnDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryReturnDto> getAllCategories() {
        return categoryMapper.buildListCategoryReturnDto((List<CategoryEntity>) categoryRepo.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryReturnDto getCategoryById(Long id) {
        CategoryEntity category = categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        return categoryMapper.buildCategoryReturnDto(category);
    }

    @Override
    @Transactional
    public CategoryReturnDto updateCategory(Long id, CategoryCreateDto dto) {
        CategoryEntity existingCategory = categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        
        existingCategory.setName(dto.getName());
        CategoryEntity savedCategory = categoryRepo.save(existingCategory);
        
        return categoryMapper.buildCategoryReturnDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }
}


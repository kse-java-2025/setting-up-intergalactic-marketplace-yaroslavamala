package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.config.MappersTestConfiguration;
import com.cosmocats.cosmomarket.dto.category.CategoryCreateDto;
import com.cosmocats.cosmomarket.dto.category.CategoryReturnDto;
import com.cosmocats.cosmomarket.exception.CategoryNotFoundException;
import com.cosmocats.cosmomarket.repository.CategoryRepository;
import com.cosmocats.cosmomarket.repository.entity.CategoryEntity;
import com.cosmocats.cosmomarket.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CategoryServiceImpl.class})
@Import(MappersTestConfiguration.class)
@DisplayName("Category Service Tests")
public class CategoryServiceImplTest {

    private static final Long CATEGORY_ID = 1L;
    private static final Long ANOTHER_CATEGORY_ID = 2L;
    private static final String CATEGORY_NAME = "CLOTHES";
    private static final String UPDATED_CATEGORY_NAME = "UPDATED CLOTHES";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found";

    @MockitoBean
    private CategoryRepository repo;

    @Captor
    private ArgumentCaptor<CategoryEntity> categoryCaptor;

    @Autowired
    private CategoryServiceImpl categoryService;

    private static CategoryEntity buildCategory(Long id, String name) {
        return CategoryEntity.builder()
                .id(id)
                .name(name)
                .build();
    }

    private static CategoryCreateDto buildCategoryCreateDto(String name) {
        return CategoryCreateDto.builder()
                .name(name)
                .build();
    }

    private static Stream<CategoryCreateDto> provideCategoryCreateDtos() {
        return Stream.of(
                buildCategoryCreateDto("CLOTHES"),
                buildCategoryCreateDto("ELECTRONICS"),
                buildCategoryCreateDto("FOOD")
        );
    }

    @ParameterizedTest
    @MethodSource("provideCategoryCreateDtos")
    @DisplayName("Should create new category successfully for different inputs")
    void shouldCreateNewCategorySuccessfully(CategoryCreateDto createDto) {
        CategoryEntity savedCategory = buildCategory(CATEGORY_ID, createDto.getName());

        when(repo.save(any(CategoryEntity.class))).thenReturn(savedCategory);

        CategoryReturnDto result = categoryService.createNewCategory(createDto);

        assertNotNull(result);
        assertEquals(CATEGORY_ID, result.getId());
        assertEquals(createDto.getName(), result.getName());

        verify(repo, times(1)).save(categoryCaptor.capture());
        CategoryEntity captured = categoryCaptor.getValue();
        assertNotNull(captured);
        assertEquals(createDto.getName(), captured.getName());
    }

    @Test
    @DisplayName("Should return all categories successfully")
    void shouldReturnAllCategoriesSuccessfully() {
        CategoryEntity category1 = buildCategory(CATEGORY_ID, CATEGORY_NAME);
        CategoryEntity category2 = buildCategory(ANOTHER_CATEGORY_ID, UPDATED_CATEGORY_NAME);
        List<CategoryEntity> allCategories = List.of(category1, category2);

        when(repo.findAll()).thenReturn(allCategories);

        List<CategoryReturnDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(CATEGORY_ID, result.get(0).getId());
        assertEquals(CATEGORY_NAME, result.get(0).getName());
        assertEquals(ANOTHER_CATEGORY_ID, result.get(1).getId());
        assertEquals(UPDATED_CATEGORY_NAME, result.get(1).getName());
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void shouldReturnEmptyListWhenNoCategories() {
        when(repo.findAll()).thenReturn(List.of());

        List<CategoryReturnDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get category by id successfully")
    void shouldGetCategoryByIdSuccessfully() {
        CategoryEntity category = buildCategory(CATEGORY_ID, CATEGORY_NAME);

        when(repo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        CategoryReturnDto result = categoryService.getCategoryById(CATEGORY_ID);

        assertNotNull(result);
        assertEquals(CATEGORY_ID, result.getId());
        assertEquals(CATEGORY_NAME, result.getName());
        verify(repo, times(1)).findById(CATEGORY_ID);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException with correct message when category not found by id")
    void shouldThrowExceptionWhenCategoryNotFoundById() {
        when(repo.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(CATEGORY_ID));

        assertTrue(exception.getMessage().contains(CATEGORY_NOT_FOUND_MESSAGE));
        assertTrue(exception.getMessage().contains(CATEGORY_ID.toString()));
        verify(repo, times(1)).findById(CATEGORY_ID);
    }

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategorySuccessfully() {
        CategoryEntity existingCategory = buildCategory(CATEGORY_ID, CATEGORY_NAME);
        CategoryCreateDto updateDto = buildCategoryCreateDto(UPDATED_CATEGORY_NAME);
        CategoryEntity updatedCategory = buildCategory(CATEGORY_ID, UPDATED_CATEGORY_NAME);

        when(repo.findById(CATEGORY_ID)).thenReturn(Optional.of(existingCategory));
        when(repo.save(any(CategoryEntity.class))).thenReturn(updatedCategory);

        CategoryReturnDto result = categoryService.updateCategory(CATEGORY_ID, updateDto);

        assertNotNull(result);
        assertEquals(CATEGORY_ID, result.getId());
        assertEquals(UPDATED_CATEGORY_NAME, result.getName());

        verify(repo, times(1)).findById(CATEGORY_ID);
        verify(repo, times(1)).save(categoryCaptor.capture());

        CategoryEntity capturedCategory = categoryCaptor.getValue();
        assertNotNull(capturedCategory);
        assertEquals(CATEGORY_ID, capturedCategory.getId());
        assertEquals(UPDATED_CATEGORY_NAME, capturedCategory.getName());
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when updating non-existent category")
    void shouldThrowExceptionWhenUpdatingNonExistentCategory() {
        CategoryCreateDto updateDto = buildCategoryCreateDto(UPDATED_CATEGORY_NAME);

        when(repo.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(CATEGORY_ID, updateDto));

        assertTrue(exception.getMessage().contains(CATEGORY_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(CATEGORY_ID);
        verify(repo, never()).save(any(CategoryEntity.class));
    }

    @Test
    @DisplayName("Should delete existing category successfully")
    void shouldDeleteExistingCategorySuccessfully() {
        categoryService.deleteCategory(CATEGORY_ID);

        verify(repo, times(1)).deleteById(CATEGORY_ID);
    }
}

package com.cosmocats.cosmomarket.web;

import com.cosmocats.cosmomarket.config.MappersTestConfiguration;
import com.cosmocats.cosmomarket.dto.category.CategoryCreateDto;
import com.cosmocats.cosmomarket.dto.category.CategoryReturnDto;
import com.cosmocats.cosmomarket.exception.CategoryNotFoundException;
import com.cosmocats.cosmomarket.service.CategoryServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.stream.Stream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MappersTestConfiguration.class)
@DisplayName("Category Controller Integration Tests")
public class CategoryControllerIT {

    private static final Long CATEGORY_ID = 1L;
    private static final Long ANOTHER_CATEGORY_ID = 2L;
    private static final String CATEGORY_NAME = "CLOTHES";
    private static final String UPDATED_CATEGORY_NAME = "ELECTRONICS";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found";
    private static final String VALIDATION_FAILED_MESSAGE = "Validation failed";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryServiceInterface categoryService;

    @BeforeEach
    void setUp() {
        reset(categoryService);
    }

    private static CategoryCreateDto buildCategoryCreateDto(String name) {
        return CategoryCreateDto.builder()
                .name(name)
                .build();
    }

    private static CategoryReturnDto buildCategoryReturnDto(Long id, String name) {
        return CategoryReturnDto.builder()
                .id(id)
                .name(name)
                .build();
    }

    private static Stream<CategoryCreateDto> provideValidCategoryCreateDtos() {
        return Stream.of(
                buildCategoryCreateDto("CLOTHES"),
                buildCategoryCreateDto("ELECTRONICS"),
                buildCategoryCreateDto("FOOD")
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidCategoryCreateDtos")
    @DisplayName("Should create category successfully with valid data")
    @SneakyThrows
    void shouldCreateCategoryWithValidData(CategoryCreateDto createDto) {
        CategoryReturnDto returnDto = buildCategoryReturnDto(CATEGORY_ID, createDto.getName());
        when(categoryService.createNewCategory(any(CategoryCreateDto.class))).thenReturn(returnDto);

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(createDto.getName()));
    }

    @Test
    @DisplayName("Should reject request with null category name")
    @SneakyThrows
    void shouldRejectNullCategoryName() {
        CategoryCreateDto invalidDto = CategoryCreateDto.builder()
                .name(null)
                .build();

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .accept(MediaType.APPLICATION_PROBLEM_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value(VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    @DisplayName("Should return all categories successfully")
    @SneakyThrows
    void shouldReturnAllCategoriesSuccessfully() {
        when(categoryService.getAllCategories()).thenReturn(List.of(buildCategoryReturnDto(CATEGORY_ID, CATEGORY_NAME),
                buildCategoryReturnDto(ANOTHER_CATEGORY_ID, UPDATED_CATEGORY_NAME)));

        mockMvc.perform(get("/api/v1/categories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(CATEGORY_ID))
                .andExpect(jsonPath("$[0].name").value(CATEGORY_NAME))
                .andExpect(jsonPath("$[1].id").value(ANOTHER_CATEGORY_ID))
                .andExpect(jsonPath("$[1].name").value(UPDATED_CATEGORY_NAME))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    @SneakyThrows
    void shouldReturnEmptyList() {
        when(categoryService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/categories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should get category by id successfully")
    @SneakyThrows
    void shouldGetCategoryByIdSuccessfully() {
        CategoryReturnDto returnDto = buildCategoryReturnDto(CATEGORY_ID, CATEGORY_NAME);
        when(categoryService.getCategoryById(CATEGORY_ID)).thenReturn(returnDto);

        mockMvc.perform(get("/api/v1/categories/{id}", CATEGORY_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(CATEGORY_NAME));
    }

    @Test
    @DisplayName("Should return 404 when category not found by id")
    @SneakyThrows
    void shouldReturn404WhenCategoryNotFound() {
        when(categoryService.getCategoryById(CATEGORY_ID)).thenThrow(new CategoryNotFoundException(CATEGORY_ID));

        mockMvc.perform(get("/api/v1/categories/{id}", CATEGORY_ID)
                .accept(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(CATEGORY_NOT_FOUND_MESSAGE))
                .andExpect(jsonPath("$.detail").value(CATEGORY_NOT_FOUND_MESSAGE + ": " + CATEGORY_ID));
    }

    @Test
    @DisplayName("Should update category successfully with valid data")
    @SneakyThrows
    void shouldUpdateCategorySuccessfully() {
        CategoryCreateDto updateDto = buildCategoryCreateDto(UPDATED_CATEGORY_NAME);
        CategoryReturnDto returnDto = buildCategoryReturnDto(CATEGORY_ID, UPDATED_CATEGORY_NAME);

        when(categoryService.updateCategory(eq(CATEGORY_ID), any(CategoryCreateDto.class))).thenReturn(returnDto);

        mockMvc.perform(put("/api/v1/categories/{id}", CATEGORY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(UPDATED_CATEGORY_NAME));
    }

    @Test
    @DisplayName("Should reject update with null category name")
    @SneakyThrows
    void shouldRejectUpdateWithNullName() {
        CategoryCreateDto invalidDto = CategoryCreateDto.builder()
                .name(null)
                .build();

        mockMvc.perform(put("/api/v1/categories/{id}", CATEGORY_ID)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .accept(MediaType.APPLICATION_PROBLEM_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value(VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent category")
    @SneakyThrows
    void shouldReturn404WhenUpdatingNonExistentCategory() {
        CategoryCreateDto updateDto = buildCategoryCreateDto(UPDATED_CATEGORY_NAME);
        when(categoryService.updateCategory(eq(CATEGORY_ID), any(CategoryCreateDto.class)))
                .thenThrow(new CategoryNotFoundException(CATEGORY_ID));

        mockMvc.perform(put("/api/v1/categories/{id}", CATEGORY_ID)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .accept(MediaType.APPLICATION_PROBLEM_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(CATEGORY_NOT_FOUND_MESSAGE))
                .andExpect(jsonPath("$.detail").value(CATEGORY_NOT_FOUND_MESSAGE + ": " + CATEGORY_ID));
    }

    @Test
    @DisplayName("Should delete category successfully")
    @SneakyThrows
    void shouldDeleteCategorySuccessfully() {
        mockMvc.perform(delete("/api/v1/categories/{id}", CATEGORY_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

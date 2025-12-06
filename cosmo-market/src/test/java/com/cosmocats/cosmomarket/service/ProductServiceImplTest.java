package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.config.MappersTestConfiguration;
import com.cosmocats.cosmomarket.dto.product.ProductCreateDto;
import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import com.cosmocats.cosmomarket.dto.product.ProductUpdateDto;
import com.cosmocats.cosmomarket.exception.CategoryNotFoundException;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.repository.CategoryRepository;
import com.cosmocats.cosmomarket.repository.ProductRepository;
import com.cosmocats.cosmomarket.repository.entity.CategoryEntity;
import com.cosmocats.cosmomarket.repository.entity.ProductEntity;
import com.cosmocats.cosmomarket.service.impl.ProductServiceImpl;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ProductServiceImpl.class})
@Import(MappersTestConfiguration.class)
@DisplayName("Product Service Tests")
public class ProductServiceImplTest {

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID ANOTHER_PRODUCT_ID = UUID.randomUUID();
    private static final String PRODUCT_NAME = "Cosmic Socks";
    private static final String UPDATED_PRODUCT_NAME = "Updated Cosmic Socks";
    private static final String PRODUCT_DESCRIPTION = "Comfortable space socks";
    private static final BigDecimal PRICE = BigDecimal.valueOf(10.5);
    private static final BigDecimal UPDATED_PRICE = BigDecimal.valueOf(15.99);
    private static final Integer AVAILABLE_QUANTITY = 100;
    private static final Integer UPDATED_QUANTITY = 50;
    private static final Long CATEGORY_ID = 10L;
    private static final String CATEGORY_NAME = "CLOTHES";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";

    @MockitoBean
    private ProductRepository repo;

    @MockitoBean
    private CategoryRepository categoryRepo;

    @Captor
    private ArgumentCaptor<ProductEntity> productCaptor;

    @Autowired
    private ProductServiceImpl productService;

    private static CategoryEntity buildCategory() {
        return CategoryEntity.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .build();
    }

    private static ProductEntity buildProduct(UUID id, String name, BigDecimal price) {
        return ProductEntity.builder()
                .id(id)
                .name(name)
                .description(PRODUCT_DESCRIPTION)
                .category(buildCategory())
                .availableQuantity(AVAILABLE_QUANTITY)
                .price(price)
                .build();
    }

    private static ProductCreateDto buildProductCreateDto(String name, BigDecimal price) {
        return ProductCreateDto.builder()
                .name(name)
                .description(PRODUCT_DESCRIPTION)
                .categoryId(CATEGORY_ID)
                .availableQuantity(AVAILABLE_QUANTITY)
                .price(price)
                .build();
    }

    private static ProductUpdateDto buildProductUpdateDto() {
        return ProductUpdateDto.builder()
                .name(UPDATED_PRODUCT_NAME)
                .price(UPDATED_PRICE)
                .availableQuantity(UPDATED_QUANTITY)
                .build();
    }

    private static Stream<ProductCreateDto> provideProductCreateDtos() {
        return Stream.of(
                buildProductCreateDto("Cosmic Socks", BigDecimal.valueOf(10.5)),
                buildProductCreateDto("Space Hat", BigDecimal.valueOf(25.0)),
                buildProductCreateDto("Galaxy Jacket", BigDecimal.valueOf(150.0))
        );
    }

    @ParameterizedTest
    @MethodSource("provideProductCreateDtos")
    @DisplayName("Should create new product successfully for different inputs")
    void shouldCreateNewProductSuccessfully(ProductCreateDto createDto) {
        ProductEntity savedProduct = buildProduct(PRODUCT_ID, createDto.getName(), createDto.getPrice());
        CategoryEntity category = buildCategory();

        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(repo.save(any(ProductEntity.class))).thenReturn(savedProduct);

        ProductReturnDto result = productService.createNewProduct(createDto);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());
        assertEquals(CATEGORY_ID, result.getCategoryId());

        verify(categoryRepo, times(1)).findById(CATEGORY_ID);
        verify(repo, times(1)).save(productCaptor.capture());
        ProductEntity captured = productCaptor.getValue();
        assertNotNull(captured);
        assertAll(
                () -> assertEquals(createDto.getName(), captured.getName()),
                () -> assertEquals(createDto.getPrice(), captured.getPrice()),
                () -> assertEquals(CATEGORY_ID, captured.getCategory().getId())
        );
    }

    @Test
    @DisplayName("Should throw exception when creating product with non-existent category")
    void shouldThrowExceptionWhenCreatingProductWithNonExistentCategory() {
        ProductCreateDto createDto = buildProductCreateDto(PRODUCT_NAME, PRICE);
        
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> productService.createNewProduct(createDto));

        verify(categoryRepo, times(1)).findById(CATEGORY_ID);
        verify(repo, never()).save(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Should return all products successfully")
    void shouldReturnAllProductsSuccessfully() {
        ProductEntity product1 = buildProduct(PRODUCT_ID, PRODUCT_NAME, PRICE);
        ProductEntity product2 = buildProduct(ANOTHER_PRODUCT_ID, UPDATED_PRODUCT_NAME, PRICE);
        List<ProductEntity> allProducts = List.of(product1, product2);
        CategoryEntity category = buildCategory();

        when(repo.findAll()).thenReturn(allProducts);
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        List<ProductReturnDto> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(CATEGORY_ID, result.get(0).getCategoryId());
        assertEquals(CATEGORY_ID, result.get(1).getCategoryId());
        verify(repo, times(1)).findAll();
    }

    @Test 
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProducts() {
        when(repo.findAll()).thenReturn(List.of());

        List<ProductReturnDto> result = productService.getAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get product by id successfully")
    void shouldGetProductByIdSuccessfully() {
        ProductEntity product = buildProduct(PRODUCT_ID, PRODUCT_NAME, PRICE);
        CategoryEntity category = buildCategory();

        when(repo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        ProductReturnDto result = productService.getProductById(PRODUCT_ID);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());
        assertEquals(PRODUCT_NAME, result.getName());
        assertEquals(CATEGORY_ID, result.getCategoryId());
        verify(repo, times(1)).findById(PRODUCT_ID);
    }

    @Test
    @DisplayName("Should throws ProductNotFoundException with correct message when product not found by id")
    void shouldThrowExceptionWhenProductNotFoundById() {
        when(repo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.getProductById(PRODUCT_ID));

        assertTrue(exception.getMessage().contains(PRODUCT_NOT_FOUND_MESSAGE));
        assertTrue(exception.getMessage().contains(PRODUCT_ID.toString()));
        verify(repo, times(1)).findById(PRODUCT_ID);
    }

    @Test
    @DisplayName("Should update provided product fields successfully and no change others fields")
    void shouldUpdateProductSuccessfully() {
        ProductEntity existingProduct = buildProduct(PRODUCT_ID, PRODUCT_NAME, PRICE);
        ProductUpdateDto updateDto = buildProductUpdateDto();
        ProductEntity updatedProduct = ProductEntity.builder()
                .id(existingProduct.getId())
                .name(updateDto.getName())
                .description(existingProduct.getDescription())
                .category(existingProduct.getCategory())
                .availableQuantity(updateDto.getAvailableQuantity())
                .price(updateDto.getPrice())
                .build();
        CategoryEntity category = buildCategory();

        when(repo.findById(PRODUCT_ID)).thenReturn(Optional.of(existingProduct));
        when(repo.save(any(ProductEntity.class))).thenReturn(updatedProduct);
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        ProductReturnDto result = productService.updateProduct(PRODUCT_ID, updateDto);

        assertNotNull(result);
        assertAll(
                () -> assertEquals(PRODUCT_ID, result.getId()),
                () -> assertEquals(UPDATED_PRODUCT_NAME, result.getName()),
                () -> assertEquals(UPDATED_PRICE, result.getPrice()),
                () -> assertEquals(PRODUCT_DESCRIPTION, result.getDescription()),
                () -> assertEquals(CATEGORY_ID, result.getCategoryId()),
                () -> assertEquals(UPDATED_QUANTITY, result.getAvailableQuantity())
        );

        verify(repo, times(1)).findById(PRODUCT_ID);
        verify(repo, times(1)).save(productCaptor.capture());

        ProductEntity capturedProduct = productCaptor.getValue();
        assertNotNull(capturedProduct);
        assertAll(
                () -> assertEquals(PRODUCT_ID, capturedProduct.getId()),
                () -> assertEquals(UPDATED_PRODUCT_NAME, capturedProduct.getName()),
                () -> assertEquals(UPDATED_PRICE, capturedProduct.getPrice()),
                () -> assertEquals(UPDATED_QUANTITY, capturedProduct.getAvailableQuantity())
        );
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating not existing product")
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        ProductUpdateDto updateDto = buildProductUpdateDto();

        when(repo.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(PRODUCT_ID, updateDto));

        assertTrue(exception.getMessage().contains(PRODUCT_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(PRODUCT_ID);
        verify(repo, never()).save(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Should delete existing product successfully")
    void shouldDeleteExistingProductSuccessfully() {
        productService.deleteProduct(PRODUCT_ID);

        verify(repo, times(1)).deleteById(PRODUCT_ID);
    }

    @Test
    @DisplayName("Should find product by natural ID successfully")
    void shouldFindProductByNaturalId() {
        ProductEntity product = buildProduct(PRODUCT_ID, PRODUCT_NAME, PRICE);
        when(repo.findByNaturalId(PRODUCT_ID)).thenReturn(Optional.of(product));

        Optional<ProductEntity> result = repo.findByNaturalId(PRODUCT_ID);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.get().getId());
        assertEquals(PRODUCT_NAME, result.get().getName());
        verify(repo, times(1)).findByNaturalId(PRODUCT_ID);
    }

    @Test
    @DisplayName("Should return empty when finding by non-existent natural ID")
    void shouldReturnEmptyWhenFindingByNonExistentNaturalId() {
        UUID nonExistentId = UUID.randomUUID();
        when(repo.findByNaturalId(nonExistentId)).thenReturn(Optional.empty());

        Optional<ProductEntity> result = repo.findByNaturalId(nonExistentId);

        assertNotNull(result);
        verify(repo, times(1)).findByNaturalId(nonExistentId);
    }
}

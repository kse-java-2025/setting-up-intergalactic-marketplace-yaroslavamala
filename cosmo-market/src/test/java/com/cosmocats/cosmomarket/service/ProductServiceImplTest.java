package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.config.MappersTestConfiguration;
import com.cosmocats.cosmomarket.domain.category.Category;
import com.cosmocats.cosmomarket.domain.product.Product;
import com.cosmocats.cosmomarket.dto.product.ProductCreateDto;
import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import com.cosmocats.cosmomarket.dto.product.ProductUpdateDto;
import com.cosmocats.cosmomarket.repository.ProductRepositoryInterface;
import com.cosmocats.cosmomarket.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ProductServiceImpl.class})
@Import(MappersTestConfiguration.class)
@DisplayName("Product Service Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    private static final Category CATEGORY = Category.CLOTHES;

    @MockitoBean
    private ProductRepositoryInterface repo;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Autowired
    private ProductServiceImpl productService;

    private static Product buildProduct(String name, BigDecimal price) {
        return Product.builder()
                .id(PRODUCT_ID)
                .name(name)
                .description(PRODUCT_DESCRIPTION)
                .category(CATEGORY)
                .availableQuantity(AVAILABLE_QUANTITY)
                .price(price)
                .build();
    }

    private static Product buildProduct(UUID id, String name) {
        return Product.builder()
                .id(id)
                .name(name)
                .description(PRODUCT_DESCRIPTION)
                .category(CATEGORY)
                .availableQuantity(AVAILABLE_QUANTITY)
                .price(PRICE)
                .build();
    }

    private static ProductCreateDto buildProductCreateDto(String name, BigDecimal price) {
        return ProductCreateDto.builder()
                .name(name)
                .description(PRODUCT_DESCRIPTION)
                .category(CATEGORY)
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
    @Order(1)
    @DisplayName("Should create new product successfully for different inputs")
    void shouldCreateNewProductSuccessfully(ProductCreateDto createDto) {
        Product savedProduct = buildProduct(createDto.getName(), createDto.getPrice());

        when(repo.saveProduct(any(Product.class))).thenReturn(savedProduct);

        ProductReturnDto result = productService.createNewProduct(createDto);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());

        verify(repo, times(1)).saveProduct(productCaptor.capture());
        Product captured = productCaptor.getValue();
        assertNotNull(captured);
        assertAll(
                () -> assertEquals(createDto.getName(), captured.getName()),
                () -> assertEquals(createDto.getPrice(), captured.getPrice())
        );
    }

    @Test
    @Order(2)
    @DisplayName("Should return all products successfully")
    void shouldReturnAllProductsSuccessfully() {
        Product product1 = buildProduct(PRODUCT_ID, PRODUCT_NAME);
        Product product2 = buildProduct(ANOTHER_PRODUCT_ID, UPDATED_PRODUCT_NAME);
        List<Product> allProducts = List.of(product1, product2);

        when(repo.getAllProducts()).thenReturn(allProducts);

        List<ProductReturnDto> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repo, times(1)).getAllProducts();
    }

    @Test
    @Order(3)
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProducts() {
        when(repo.getAllProducts()).thenReturn(List.of());

        List<ProductReturnDto> result = productService.getAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo, times(1)).getAllProducts();
    }

    @Test
    @Order(4)
    @DisplayName("Should get product by id successfully")
    void shouldGetProductByIdSuccessfully() {
        Product product = buildProduct(PRODUCT_NAME, PRICE);

        when(repo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        ProductReturnDto result = productService.getProductById(PRODUCT_ID);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());
        assertEquals(PRODUCT_NAME, result.getName());
        verify(repo, times(1)).findById(PRODUCT_ID);
    }

    @Test
    @Order(5)
    @DisplayName("Should throws NoSuchElementException with correct message when product not found by id")
    void shouldThrowExceptionWhenProductNotFoundById() {
        when(repo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> productService.getProductById(PRODUCT_ID));

        assertTrue(exception.getMessage().contains("Product not found"));
        assertTrue(exception.getMessage().contains(PRODUCT_ID.toString()));
        verify(repo, times(1)).findById(PRODUCT_ID);
    }

    @Test
    @Order(6)
    @DisplayName("Should update provided product fields successfully and no change others fields")
    void shouldUpdateProductSuccessfully() {
        Product existingProduct = buildProduct(PRODUCT_NAME, PRICE);
        ProductUpdateDto updateDto = buildProductUpdateDto();
        Product updatedProduct = Product.builder()
                .id(existingProduct.getId())
                .name(updateDto.getName())
                .description(existingProduct.getDescription())
                .category(existingProduct.getCategory())
                .availableQuantity(updateDto.getAvailableQuantity())
                .price(updateDto.getPrice())
                .build();

        when(repo.findById(PRODUCT_ID)).thenReturn(Optional.of(existingProduct));
        when(repo.saveProduct(any(Product.class))).thenReturn(updatedProduct);

        ProductReturnDto result = productService.updateProduct(PRODUCT_ID, updateDto);

        assertNotNull(result);
        assertAll(
                () -> assertEquals(PRODUCT_ID, result.getId()),
                () -> assertEquals(UPDATED_PRODUCT_NAME, result.getName()),
                () -> assertEquals(UPDATED_PRICE, result.getPrice()),
                () -> assertEquals(PRODUCT_DESCRIPTION, result.getDescription()),
                () -> assertEquals(CATEGORY, result.getCategory()),
                () -> assertEquals(UPDATED_QUANTITY, result.getAvailableQuantity())
        );

        verify(repo, times(1)).findById(PRODUCT_ID);
        verify(repo, times(1)).saveProduct(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertNotNull(capturedProduct);
        assertAll(
                () -> assertEquals(PRODUCT_ID, capturedProduct.getId()),
                () -> assertEquals(UPDATED_PRODUCT_NAME, capturedProduct.getName()),
                () -> assertEquals(UPDATED_PRICE,  capturedProduct.getPrice()),
                () -> assertEquals(UPDATED_QUANTITY,  capturedProduct.getAvailableQuantity())
        );

    }

    @Test
    @Order(7)
    @DisplayName("Should throw NoSuchElementException when updating not existing product")
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        ProductUpdateDto updateDto = buildProductUpdateDto();

        when(repo.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> productService.updateProduct(PRODUCT_ID, updateDto));

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(repo, times(1)).findById(PRODUCT_ID);
        verify(repo, never()).saveProduct(any(Product.class));
    }

    @Test
    @Order(8)
    @DisplayName("Should delete existing product successfully")
    void shouldDeleteExistingProductSuccessfully() {
        when(repo.existsById(PRODUCT_ID)).thenReturn(true);

        productService.deleteProduct(PRODUCT_ID);

        verify(repo, times(1)).existsById(PRODUCT_ID);
        verify(repo, times(1)).deleteById(PRODUCT_ID);
    }

    @Test
    @Order(9)
    @DisplayName("Should throw NoSuchElementException when deleting not existing product")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        when(repo.existsById(PRODUCT_ID)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> productService.deleteProduct(PRODUCT_ID));

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(repo, times(1)).existsById(PRODUCT_ID);
        verify(repo, never()).deleteById(PRODUCT_ID);
    }
}

package com.cosmocats.cosmomarket.web;

import com.cosmocats.cosmomarket.config.MappersTestConfiguration;
import com.cosmocats.cosmomarket.domain.category.Category;
import com.cosmocats.cosmomarket.dto.product.ProductCreateDto;
import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import com.cosmocats.cosmomarket.dto.product.ProductUpdateDto;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.service.ProductServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;
import static org.mockito.ArgumentMatchers.any;
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
@DisplayName("Product Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerIT {

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID ANOTHER_PRODUCT_ID = UUID.randomUUID();
    private static final String PRODUCT_NAME = "Cosmic Socks";
    private static final String PRODUCT_DESCRIPTION = "Comfortable space socks";
    private static final String UPDATED_PRODUCT_NAME = "Updated Cosmic Socks";
    private static final BigDecimal PRICE = BigDecimal.valueOf(10.5);
    private static final BigDecimal UPDATED_PRICE = BigDecimal.valueOf(15.99);
    private static final Integer AVAILABLE_QUANTITY = 100;
    private static final Integer UPDATED_QUANTITY = 50;
    private static final Category CATEGORY = Category.CLOTHES;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductServiceInterface productService;

    @BeforeEach
    void setUp() {
        reset(productService);
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

    private static ProductCreateDto buildInvalidProductCreateDto(String name, String description,
                                                                  Category category, Integer quantity, BigDecimal price) {
        return ProductCreateDto.builder()
                .name(name)
                .description(description)
                .category(category)
                .availableQuantity(quantity)
                .price(price)
                .build();
    }

    private static ProductUpdateDto buildProductUpdateDto() {
        return ProductUpdateDto.builder()
                .name(UPDATED_PRODUCT_NAME)
                .price(UPDATED_PRICE)
                .build();
    }

    private static ProductReturnDto buildProductReturnDto() {
        return ProductReturnDto.builder()
                .id(PRODUCT_ID)
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .category(CATEGORY)
                .availableQuantity(AVAILABLE_QUANTITY)
                .price(PRICE)
                .build();
    }

    private static ProductReturnDto buildProductReturnDto(UUID id, String name) {
        return ProductReturnDto.builder()
                .id(id)
                .name(name)
                .description(PRODUCT_DESCRIPTION)
                .category(CATEGORY)
                .availableQuantity(AVAILABLE_QUANTITY)
                .price(PRICE)
                .build();
    }

    private static Stream<ProductCreateDto> provideValidProductCreateDtos() {
        return Stream.of(
                buildProductCreateDto("Cosmic Socks", BigDecimal.valueOf(10.5)),
                buildProductCreateDto("Space Hat", BigDecimal.valueOf(25.0)),
                buildProductCreateDto("Galaxy Jacket", BigDecimal.valueOf(150.0))
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidProductCreateDtos")
    @Order(1)
    @DisplayName("Should create product successfully with valid data")
    @SneakyThrows
    void shouldCreateProductWithValidData(ProductCreateDto createDto) {
        ProductReturnDto returnDto = buildProductReturnDto();
        when(productService.createNewProduct(any(ProductCreateDto.class))).thenReturn(returnDto);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists());
    }

    @Test
    @Order(2)
    @DisplayName("Should reject request with null (blank) product name")
    @SneakyThrows
    void shouldRejectBlankProductName() {
        ProductCreateDto invalidDto = buildInvalidProductCreateDto(
                "",
                PRODUCT_DESCRIPTION,
                CATEGORY,
                AVAILABLE_QUANTITY,
                PRICE
        );

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("Should reject request with negative price")
    @SneakyThrows
    void shouldRejectNegativePrice() {
        ProductCreateDto invalidDto = buildInvalidProductCreateDto(
                PRODUCT_NAME,
                PRODUCT_DESCRIPTION,
                CATEGORY,
                AVAILABLE_QUANTITY,
                BigDecimal.valueOf(-5.0)
        );

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @DisplayName("Should reject request with zero price")
    @SneakyThrows
    void shouldRejectZeroPrice() {
        ProductCreateDto invalidDto = buildInvalidProductCreateDto(
                PRODUCT_NAME,
                PRODUCT_DESCRIPTION,
                CATEGORY,
                AVAILABLE_QUANTITY,
                BigDecimal.ZERO
        );

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("Should reject request with null category")
    @SneakyThrows
    void shouldRejectNullCategory() {
        ProductCreateDto invalidDto = buildInvalidProductCreateDto(
                PRODUCT_NAME,
                PRODUCT_DESCRIPTION,
                null,
                AVAILABLE_QUANTITY,
                PRICE
        );

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @DisplayName("Should reject request with negative quantity")
    @SneakyThrows
    void shouldRejectNegativeQuantity() {
        ProductCreateDto invalidDto = buildInvalidProductCreateDto(
                PRODUCT_NAME,
                PRODUCT_DESCRIPTION,
                CATEGORY,
                -50,
                PRICE
        );

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    @DisplayName("Should reject request with description exceeding 255 characters")
    @SneakyThrows
    void shouldRejectDescriptionTooLong() {
        String longDescription = "m".repeat(256);
        ProductCreateDto invalidDto = buildInvalidProductCreateDto(
                PRODUCT_NAME,
                longDescription,
                CATEGORY,
                AVAILABLE_QUANTITY,
                PRICE
        );

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    @DisplayName("Should return all products successfully")
    @SneakyThrows
    void shouldReturnAllProductsSuccessfully() {
        when(productService.getAllProducts()).thenReturn(java.util.List.of( buildProductReturnDto(PRODUCT_ID, PRODUCT_NAME),
                buildProductReturnDto(ANOTHER_PRODUCT_ID, UPDATED_PRODUCT_NAME)));

        mockMvc.perform(get("/api/products")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Order(9)
    @DisplayName("Should return empty list when no products exist")
    @SneakyThrows
    void shouldReturnEmptyList() {
        when(productService.getAllProducts()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/products")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Order(10)
    @DisplayName("Should get product by id successfully")
    @SneakyThrows
    void shouldGetProductByIdSuccessfully() {
        ProductReturnDto returnDto = buildProductReturnDto();
        when(productService.getProductById(PRODUCT_ID)).thenReturn(returnDto);

        mockMvc.perform(get("/api/products/{id}", PRODUCT_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value(PRODUCT_NAME))
                .andExpect(jsonPath("$.price").value(PRICE.doubleValue()));
    }

    @Test
    @Order(11)
    @DisplayName("Should return 404 when product not found by id")
    @SneakyThrows
    void shouldReturn404WhenProductNotFound() {
        when(productService.getProductById(PRODUCT_ID)).thenThrow(new ProductNotFoundException(PRODUCT_ID));

        mockMvc.perform(get("/api/products/{id}", PRODUCT_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    @DisplayName("Should update with valid data product successfully")
    @SneakyThrows
    void shouldUpdateProductSuccessfully() {
        ProductUpdateDto updateDto = buildProductUpdateDto();
        ProductReturnDto returnDto = ProductReturnDto.builder()
                .id(PRODUCT_ID)
                .name(UPDATED_PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .category(CATEGORY)
                .availableQuantity(UPDATED_QUANTITY)
                .price(UPDATED_PRICE)
                .build();

        when(productService.updateProduct(PRODUCT_ID, updateDto)).thenReturn(returnDto);

        mockMvc.perform(put("/api/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value(UPDATED_PRODUCT_NAME));
    }

    @Test
    @Order(13)
    @DisplayName("Should reject update with negative price")
    @SneakyThrows
    void shouldRejectUpdateWithNegativePrice() {
        ProductUpdateDto invalidDto = ProductUpdateDto.builder()
                .name(UPDATED_PRODUCT_NAME)
                .price(BigDecimal.valueOf(-10.0))
                .build();

        mockMvc.perform(put("/api/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    @DisplayName("Should reject update with negative quantity")
    @SneakyThrows
    void shouldRejectUpdateWithNegativeQuantity() {
        ProductUpdateDto invalidDto = ProductUpdateDto.builder()
                .name(UPDATED_PRODUCT_NAME)
                .availableQuantity(-50)
                .build();

        mockMvc.perform(put("/api/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(15)
    @DisplayName("Should return 404 when updating not existing product")
    @SneakyThrows
    void shouldReturn404WhenUpdatingNonExistentProduct() {
        ProductUpdateDto updateDto = buildProductUpdateDto();
        when(productService.updateProduct(PRODUCT_ID, updateDto)).thenThrow(new ProductNotFoundException(PRODUCT_ID));

        mockMvc.perform(put("/api/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(16)
    @DisplayName("Should delete product successfully")
    @SneakyThrows
    void shouldDeleteProductSuccessfully() {
        mockMvc.perform(delete("/api/products/{id}", PRODUCT_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

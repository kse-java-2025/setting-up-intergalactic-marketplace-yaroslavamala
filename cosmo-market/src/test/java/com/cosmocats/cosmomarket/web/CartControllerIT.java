package com.cosmocats.cosmomarket.web;

import com.cosmocats.cosmomarket.config.MappersTestConfiguration;
import com.cosmocats.cosmomarket.dto.cart.CartDto;
import com.cosmocats.cosmomarket.dto.cart.CartItemCreateDto;
import com.cosmocats.cosmomarket.dto.cart.CartItemUpdateDto;
import com.cosmocats.cosmomarket.exception.CartItemNotFoundException;
import com.cosmocats.cosmomarket.exception.CartNotFoundException;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.service.CartServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
@DisplayName("Cart Controller Integration Tests")
public class CartControllerIT {

    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final Integer QUANTITY = 2;
    private static final Integer UPDATED_QUANTITY = 5;
    private static final String CART_NOT_FOUND_MESSAGE = "Cart not found";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";
    private static final String CART_ITEM_NOT_FOUND_MESSAGE = "Cart item not found";
    private static final String VALIDATION_FAILED_MESSAGE = "Validation failed";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartServiceInterface cartService;

    @BeforeEach
    void setUp() {
        reset(cartService);
    }

    private static CartDto buildCartDto(UUID id) {
        return CartDto.builder()
                .id(id)
                .createdAt(OffsetDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    private static CartItemCreateDto buildCartItemCreateDto(UUID productId, Integer quantity) {
        return CartItemCreateDto.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    private static CartItemUpdateDto buildCartItemUpdateDto(Integer quantity) {
        return CartItemUpdateDto.builder()
                .quantity(quantity)
                .build();
    }

    @Test
    @DisplayName("Should create new cart successfully")
    @SneakyThrows
    void shouldCreateNewCartSuccessfully() {
        CartDto returnDto = buildCartDto(CART_ID);
        when(cartService.createNewCart()).thenReturn(returnDto);

        mockMvc.perform(post("/api/v1/carts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @DisplayName("Should return all carts successfully")
    @SneakyThrows
    void shouldReturnAllCartsSuccessfully() {
        when(cartService.getAllCart()).thenReturn(List.of(
                buildCartDto(CART_ID),
                buildCartDto(UUID.randomUUID())));

        mockMvc.perform(get("/api/v1/carts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should return empty list when no carts exist")
    @SneakyThrows
    void shouldReturnEmptyList() {
        when(cartService.getAllCart()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/carts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should get cart by id successfully")
    @SneakyThrows
    void shouldGetCartByIdSuccessfully() {
        CartDto returnDto = buildCartDto(CART_ID);
        when(cartService.getCartById(CART_ID)).thenReturn(returnDto);

        mockMvc.perform(get("/api/v1/carts/{id}", CART_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CART_ID.toString()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Should return 404 when cart not found by id")
    @SneakyThrows
    void shouldReturn404WhenCartNotFound() {
        when(cartService.getCartById(CART_ID)).thenThrow(new CartNotFoundException(CART_ID));

        mockMvc.perform(get("/api/v1/carts/{id}", CART_ID)
                .accept(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(CART_NOT_FOUND_MESSAGE))
                .andExpect(jsonPath("$.detail").value(CART_NOT_FOUND_MESSAGE + ": " + CART_ID));
    }

    @Test
    @DisplayName("Should add product to cart successfully")
    @SneakyThrows
    void shouldAddProductToCartSuccessfully() {
        CartItemCreateDto createDto = buildCartItemCreateDto(PRODUCT_ID, QUANTITY);
        CartDto returnDto = buildCartDto(CART_ID);
        
        when(cartService.addProductToCart(eq(CART_ID), eq(PRODUCT_ID), eq(QUANTITY))).thenReturn(returnDto);

        mockMvc.perform(post("/api/v1/carts/{cartId}/items", CART_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CART_ID.toString()));
    }

    @Test
    @DisplayName("Should reject add product with null product id")
    @SneakyThrows
    void shouldRejectAddProductWithNullProductId() {
        CartItemCreateDto invalidDto = CartItemCreateDto.builder()
                .productId(null)
                .quantity(QUANTITY)
                .build();

        mockMvc.perform(post("/api/v1/carts/{cartId}/items", CART_ID)
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
    @DisplayName("Should reject add product with invalid quantity")
    @SneakyThrows
    void shouldRejectAddProductWithInvalidQuantity() {
        CartItemCreateDto invalidDto = CartItemCreateDto.builder()
                .productId(PRODUCT_ID)
                .quantity(0)
                .build();

        mockMvc.perform(post("/api/v1/carts/{cartId}/items", CART_ID)
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
    @DisplayName("Should return 404 when adding product to non-existent cart")
    @SneakyThrows
    void shouldReturn404WhenAddingProductToNonExistentCart() {
        CartItemCreateDto createDto = buildCartItemCreateDto(PRODUCT_ID, QUANTITY);
        when(cartService.addProductToCart(eq(CART_ID), any(), any()))
                .thenThrow(new CartNotFoundException(CART_ID));

        mockMvc.perform(post("/api/v1/carts/{cartId}/items", CART_ID)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .accept(MediaType.APPLICATION_PROBLEM_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(CART_NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should return 404 when adding non-existent product to cart")
    @SneakyThrows
    void shouldReturn404WhenAddingNonExistentProduct() {
        CartItemCreateDto createDto = buildCartItemCreateDto(PRODUCT_ID, QUANTITY);
        when(cartService.addProductToCart(eq(CART_ID), eq(PRODUCT_ID), any()))
                .thenThrow(new ProductNotFoundException(PRODUCT_ID));

        mockMvc.perform(post("/api/v1/carts/{cartId}/items", CART_ID)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .accept(MediaType.APPLICATION_PROBLEM_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(PRODUCT_NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should update cart item quantity successfully")
    @SneakyThrows
    void shouldUpdateCartItemQuantitySuccessfully() {
        CartItemUpdateDto updateDto = buildCartItemUpdateDto(UPDATED_QUANTITY);
        CartDto returnDto = buildCartDto(CART_ID);
        
        when(cartService.updateCartItemQuantity(eq(CART_ID), eq(ITEM_ID), eq(UPDATED_QUANTITY)))
                .thenReturn(returnDto);

        mockMvc.perform(put("/api/v1/carts/{cartId}/items/{itemId}", CART_ID, ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CART_ID.toString()));
    }

    @Test
    @DisplayName("Should reject update with invalid quantity")
    @SneakyThrows
    void shouldRejectUpdateWithInvalidQuantity() {
        CartItemUpdateDto invalidDto = CartItemUpdateDto.builder()
                .quantity(0)
                .build();

        mockMvc.perform(put("/api/v1/carts/{cartId}/items/{itemId}", CART_ID, ITEM_ID)
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
    @DisplayName("Should return 404 when updating non-existent cart item")
    @SneakyThrows
    void shouldReturn404WhenUpdatingNonExistentItem() {
        CartItemUpdateDto updateDto = buildCartItemUpdateDto(UPDATED_QUANTITY);
        when(cartService.updateCartItemQuantity(eq(CART_ID), eq(ITEM_ID), any()))
                .thenThrow(new CartItemNotFoundException(ITEM_ID, CART_ID));

        mockMvc.perform(put("/api/v1/carts/{cartId}/items/{itemId}", CART_ID, ITEM_ID)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .accept(MediaType.APPLICATION_PROBLEM_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(CART_ITEM_NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should remove item from cart successfully")
    @SneakyThrows
    void shouldRemoveItemFromCartSuccessfully() {
        CartDto returnDto = buildCartDto(CART_ID);
        when(cartService.removeItemFromCart(CART_ID, ITEM_ID)).thenReturn(returnDto);

        mockMvc.perform(delete("/api/v1/carts/{cartId}/items/{itemId}", CART_ID, ITEM_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CART_ID.toString()));
    }

    @Test
    @DisplayName("Should delete cart successfully")
    @SneakyThrows
    void shouldDeleteCartSuccessfully() {
        mockMvc.perform(delete("/api/v1/carts/{id}", CART_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

package com.cosmocats.cosmomarket.web;

import com.cosmocats.cosmomarket.config.MappersTestConfiguration;
import com.cosmocats.cosmomarket.dto.order.OrderDto;
import com.cosmocats.cosmomarket.dto.order.OrderItemCreateDto;
import com.cosmocats.cosmomarket.dto.order.OrderItemUpdateDto;
import com.cosmocats.cosmomarket.exception.OrderItemNotFoundException;
import com.cosmocats.cosmomarket.exception.OrderNotFoundException;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.service.OrderServiceInterface;
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
import java.math.BigDecimal;
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
@DisplayName("Order Controller Integration Tests")
public class OrderControllerIT {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final Integer QUANTITY = 2;
    private static final Integer UPDATED_QUANTITY = 5;
    private static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(21.0);
    private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";
    private static final String ORDER_ITEM_NOT_FOUND_MESSAGE = "Order item not found";
    private static final String VALIDATION_FAILED_MESSAGE = "Validation failed";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderServiceInterface orderService;

    @BeforeEach
    void setUp() {
        reset(orderService);
    }

    private static OrderDto buildOrderDto(UUID id, BigDecimal totalPrice) {
        return OrderDto.builder()
                .id(id)
                .createdAt(OffsetDateTime.now())
                .totalOrderPrice(totalPrice)
                .items(new ArrayList<>())
                .build();
    }

    private static OrderItemCreateDto buildOrderItemCreateDto(UUID productId, Integer quantity) {
        return OrderItemCreateDto.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    private static OrderItemUpdateDto buildOrderItemUpdateDto(Integer quantity) {
        return OrderItemUpdateDto.builder()
                .quantity(quantity)
                .build();
    }

    @Test
    @DisplayName("Should create new order successfully")
    @SneakyThrows
    void shouldCreateNewOrderSuccessfully() {
        OrderDto returnDto = buildOrderDto(ORDER_ID, BigDecimal.ZERO);
        when(orderService.createNewOrder()).thenReturn(returnDto);

        mockMvc.perform(post("/api/v1/orders")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.totalOrderPrice").value(0));
    }

    @Test
    @DisplayName("Should return all orders successfully")
    @SneakyThrows
    void shouldReturnAllOrdersSuccessfully() {
        when(orderService.getAllOrders()).thenReturn(List.of(
                buildOrderDto(ORDER_ID, BigDecimal.ZERO),
                buildOrderDto(UUID.randomUUID(), TOTAL_PRICE)));

        mockMvc.perform(get("/api/v1/orders")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    @SneakyThrows
    void shouldReturnEmptyList() {
        when(orderService.getAllOrders()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/orders")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should get order by id successfully")
    @SneakyThrows
    void shouldGetOrderByIdSuccessfully() {
        OrderDto returnDto = buildOrderDto(ORDER_ID, BigDecimal.ZERO);
        when(orderService.getOrderById(ORDER_ID)).thenReturn(returnDto);

        mockMvc.perform(get("/api/v1/orders/{id}", ORDER_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Should return 404 when order not found by id")
    @SneakyThrows
    void shouldReturn404WhenOrderNotFound() {
        when(orderService.getOrderById(ORDER_ID)).thenThrow(new OrderNotFoundException(ORDER_ID));

        mockMvc.perform(get("/api/v1/orders/{id}", ORDER_ID)
                .accept(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(ORDER_NOT_FOUND_MESSAGE))
                .andExpect(jsonPath("$.detail").value(ORDER_NOT_FOUND_MESSAGE + ": " + ORDER_ID));
    }

    @Test
    @DisplayName("Should add product to order successfully")
    @SneakyThrows
    void shouldAddProductToOrderSuccessfully() {
        OrderItemCreateDto createDto = buildOrderItemCreateDto(PRODUCT_ID, QUANTITY);
        OrderDto returnDto = buildOrderDto(ORDER_ID, TOTAL_PRICE);
        
        when(orderService.addProductToOrder(eq(ORDER_ID), eq(PRODUCT_ID), eq(QUANTITY))).thenReturn(returnDto);

        mockMvc.perform(post("/api/v1/orders/{orderId}/items", ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ORDER_ID.toString()));
    }

    @Test
    @DisplayName("Should reject add product with null product id")
    @SneakyThrows
    void shouldRejectAddProductWithNullProductId() {
        OrderItemCreateDto invalidDto = OrderItemCreateDto.builder()
                .productId(null)
                .quantity(QUANTITY)
                .build();

        mockMvc.perform(post("/api/v1/orders/{orderId}/items", ORDER_ID)
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
    @DisplayName("Should reject add product with negative quantity")
    @SneakyThrows
    void shouldRejectAddProductWithNegativeQuantity() {
        OrderItemCreateDto invalidDto = OrderItemCreateDto.builder()
                .productId(PRODUCT_ID)
                .quantity(0)
                .build();

        mockMvc.perform(post("/api/v1/orders/{orderId}/items", ORDER_ID)
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
    @DisplayName("Should return 404 when adding product to non-existent order")
    @SneakyThrows
    void shouldReturn404WhenAddingProductToNonExistentOrder() {
        OrderItemCreateDto createDto = buildOrderItemCreateDto(PRODUCT_ID, QUANTITY);
        when(orderService.addProductToOrder(eq(ORDER_ID), any(), any()))
                .thenThrow(new OrderNotFoundException(ORDER_ID));

        mockMvc.perform(post("/api/v1/orders/{orderId}/items", ORDER_ID)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .accept(MediaType.APPLICATION_PROBLEM_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(ORDER_NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should return 404 when adding non-existent product to order")
    @SneakyThrows
    void shouldReturn404WhenAddingNonExistentProduct() {
        OrderItemCreateDto createDto = buildOrderItemCreateDto(PRODUCT_ID, QUANTITY);
        when(orderService.addProductToOrder(eq(ORDER_ID), eq(PRODUCT_ID), any()))
                .thenThrow(new ProductNotFoundException(PRODUCT_ID));

        mockMvc.perform(post("/api/v1/orders/{orderId}/items", ORDER_ID)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .accept(MediaType.APPLICATION_PROBLEM_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(PRODUCT_NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should update order item quantity successfully")
    @SneakyThrows
    void shouldUpdateOrderItemQuantitySuccessfully() {
        OrderItemUpdateDto updateDto = buildOrderItemUpdateDto(UPDATED_QUANTITY);
        OrderDto returnDto = buildOrderDto(ORDER_ID, BigDecimal.valueOf(50));
        
        when(orderService.updateOrderItemQuantity(eq(ORDER_ID), eq(ITEM_ID), eq(UPDATED_QUANTITY)))
                .thenReturn(returnDto);

        mockMvc.perform(put("/api/v1/orders/{orderId}/items/{itemId}", ORDER_ID, ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ORDER_ID.toString()));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent order item")
    @SneakyThrows
    void shouldReturn404WhenUpdatingNonExistentItem() {
        OrderItemUpdateDto updateDto = buildOrderItemUpdateDto(UPDATED_QUANTITY);
        when(orderService.updateOrderItemQuantity(eq(ORDER_ID), eq(ITEM_ID), any()))
                .thenThrow(new OrderItemNotFoundException(ITEM_ID, ORDER_ID));

        mockMvc.perform(put("/api/v1/orders/{orderId}/items/{itemId}", ORDER_ID, ITEM_ID)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .accept(MediaType.APPLICATION_PROBLEM_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(ORDER_ITEM_NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should remove item from order successfully")
    @SneakyThrows
    void shouldRemoveItemFromOrderSuccessfully() {
        OrderDto returnDto = buildOrderDto(ORDER_ID, BigDecimal.ZERO);
        when(orderService.removeItemFromOrder(ORDER_ID, ITEM_ID)).thenReturn(returnDto);

        mockMvc.perform(delete("/api/v1/orders/{orderId}/items/{itemId}", ORDER_ID, ITEM_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ORDER_ID.toString()));
    }

    @Test
    @DisplayName("Should delete order successfully")
    @SneakyThrows
    void shouldDeleteOrderSuccessfully() {
        mockMvc.perform(delete("/api/v1/orders/{id}", ORDER_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

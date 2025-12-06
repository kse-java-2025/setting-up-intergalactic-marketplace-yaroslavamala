package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.config.MappersTestConfiguration;
import com.cosmocats.cosmomarket.dto.order.OrderDto;
import com.cosmocats.cosmomarket.exception.OrderItemNotFoundException;
import com.cosmocats.cosmomarket.exception.OrderNotFoundException;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.repository.OrderRepository;
import com.cosmocats.cosmomarket.repository.ProductRepository;
import com.cosmocats.cosmomarket.repository.entity.OrderEntity;
import com.cosmocats.cosmomarket.repository.entity.OrderItemEntity;
import com.cosmocats.cosmomarket.repository.entity.ProductEntity;
import com.cosmocats.cosmomarket.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {OrderServiceImpl.class})
@Import(MappersTestConfiguration.class)
@DisplayName("Order Service Tests")
public class OrderServiceImplTest {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final String PRODUCT_NAME = "Cosmic Socks";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(10.5);
    private static final Integer QUANTITY = 2;
    private static final Integer UPDATED_QUANTITY = 5;
    private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";
    private static final String ORDER_ITEM_NOT_FOUND_MESSAGE = "Order item not found";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";


    @MockitoBean
    private OrderRepository repo;

    @MockitoBean
    private ProductRepository productRepo;

    @Captor
    private ArgumentCaptor<OrderEntity> orderCaptor;

    @Autowired
    private OrderServiceImpl orderService;

    private static ProductEntity buildProduct() {
        return ProductEntity.builder()
                .id(PRODUCT_ID)
                .name(PRODUCT_NAME)
                .price(PRODUCT_PRICE)
                .build();
    }

    private static OrderEntity buildOrder(UUID id) {
        return OrderEntity.builder()
                .id(id)
                .createdAt(OffsetDateTime.now())
                .totalPrice(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();
    }

    private static OrderItemEntity buildOrderItem(OrderEntity order, ProductEntity product, Integer quantity) {
        OrderItemEntity item = new OrderItemEntity();
        item.setId(ITEM_ID);
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setItemPrice(product.getPrice());
        return item;
    }

    @Test
    @DisplayName("Should create new order successfully")
    void shouldCreateNewOrderSuccessfully() {
        OrderEntity savedOrder = buildOrder(ORDER_ID);

        when(repo.save(any(OrderEntity.class))).thenReturn(savedOrder);

        OrderDto result = orderService.createNewOrder();

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());
        assertNotNull(result.getCreatedAt());
        assertEquals(BigDecimal.ZERO, result.getTotalOrderPrice());
        assertTrue(result.getItems().isEmpty());

        verify(repo, times(1)).save(orderCaptor.capture());
        OrderEntity captured = orderCaptor.getValue();
        assertNotNull(captured);
        assertNotNull(captured.getCreatedAt());
        assertEquals(BigDecimal.ZERO, captured.getTotalPrice());
    }

    @Test
    @DisplayName("Should return all orders successfully")
    void shouldReturnAllOrdersSuccessfully() {
        OrderEntity order1 = buildOrder(ORDER_ID);
        OrderEntity order2 = buildOrder(UUID.randomUUID());
        List<OrderEntity> allOrders = List.of(order1, order2);

        when(repo.findAll()).thenReturn(allOrders);

        List<OrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void shouldReturnEmptyListWhenNoOrders() {
        when(repo.findAll()).thenReturn(List.of());

        List<OrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get order by id successfully")
    void shouldGetOrderByIdSuccessfully() {
        OrderEntity order = buildOrder(ORDER_ID);

        when(repo.findById(ORDER_ID)).thenReturn(Optional.of(order));

        OrderDto result = orderService.getOrderById(ORDER_ID);

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());
        verify(repo, times(1)).findById(ORDER_ID);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order not found by id")
    void shouldThrowExceptionWhenOrderNotFoundById() {
        when(repo.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(ORDER_ID));

        assertTrue(exception.getMessage().contains(ORDER_NOT_FOUND_MESSAGE));
        assertTrue(exception.getMessage().contains(ORDER_ID.toString()));
        verify(repo, times(1)).findById(ORDER_ID);
    }

    @Test
    @DisplayName("Should add product to order successfully")
    void shouldAddProductToOrderSuccessfully() {
        OrderEntity order = buildOrder(ORDER_ID);
        ProductEntity product = buildProduct();
        OrderItemEntity item = buildOrderItem(order, product, QUANTITY);
        order.getItems().add(item);
        order.setTotalPrice(PRODUCT_PRICE.multiply(BigDecimal.valueOf(QUANTITY)));

        when(repo.findById(ORDER_ID)).thenReturn(Optional.of(buildOrder(ORDER_ID)));
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(repo.save(any(OrderEntity.class))).thenReturn(order);

        OrderDto result = orderService.addProductToOrder(ORDER_ID, PRODUCT_ID, QUANTITY);

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());
        assertEquals(1, result.getItems().size());
        assertEquals(PRODUCT_PRICE.multiply(BigDecimal.valueOf(QUANTITY)), result.getTotalOrderPrice());

        verify(repo, times(1)).findById(ORDER_ID);
        verify(productRepo, times(1)).findById(PRODUCT_ID);
        verify(repo, times(1)).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when adding product to non-existent order")
    void shouldThrowExceptionWhenAddingProductToNonExistentOrder() {
        when(repo.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderService.addProductToOrder(ORDER_ID, PRODUCT_ID, QUANTITY));

        assertTrue(exception.getMessage().contains(ORDER_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(ORDER_ID);
        verify(productRepo, never()).findById(any());
        verify(repo, never()).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when adding non-existent product to order")
    void shouldThrowExceptionWhenAddingNonExistentProductToOrder() {
        OrderEntity order = buildOrder(ORDER_ID);

        when(repo.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> orderService.addProductToOrder(ORDER_ID, PRODUCT_ID, QUANTITY));

        assertTrue(exception.getMessage().contains(PRODUCT_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(ORDER_ID);
        verify(productRepo, times(1)).findById(PRODUCT_ID);
        verify(repo, never()).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should update order item quantity successfully")
    void shouldUpdateOrderItemQuantitySuccessfully() {
        OrderEntity order = buildOrder(ORDER_ID);
        ProductEntity product = buildProduct();
        OrderItemEntity item = buildOrderItem(order, product, QUANTITY);
        order.getItems().add(item);
        
        OrderEntity updatedOrder = buildOrder(ORDER_ID);
        OrderItemEntity updatedItem = buildOrderItem(updatedOrder, product, UPDATED_QUANTITY);
        updatedOrder.getItems().add(updatedItem);
        updatedOrder.setTotalPrice(PRODUCT_PRICE.multiply(BigDecimal.valueOf(UPDATED_QUANTITY)));

        when(repo.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(repo.save(any(OrderEntity.class))).thenReturn(updatedOrder);

        OrderDto result = orderService.updateOrderItemQuantity(ORDER_ID, ITEM_ID, UPDATED_QUANTITY);

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());
        assertEquals(PRODUCT_PRICE.multiply(BigDecimal.valueOf(UPDATED_QUANTITY)), result.getTotalOrderPrice());

        verify(repo, times(1)).findById(ORDER_ID);
        verify(repo, times(1)).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should throw OrderItemNotFoundException when updating non-existent item")
    void shouldThrowExceptionWhenUpdatingNonExistentItem() {
        OrderEntity order = buildOrder(ORDER_ID);
        UUID nonExistentItemId = UUID.randomUUID();

        when(repo.findById(ORDER_ID)).thenReturn(Optional.of(order));

        OrderItemNotFoundException exception = assertThrows(OrderItemNotFoundException.class, () -> orderService.updateOrderItemQuantity(ORDER_ID, nonExistentItemId, UPDATED_QUANTITY));

        assertTrue(exception.getMessage().contains(ORDER_ITEM_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(ORDER_ID);
        verify(repo, never()).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should remove item from order successfully")
    void shouldRemoveItemFromOrderSuccessfully() {
        OrderEntity order = buildOrder(ORDER_ID);
        ProductEntity product = buildProduct();
        OrderItemEntity item = buildOrderItem(order, product, QUANTITY);
        order.getItems().add(item);

        OrderEntity updatedOrder = buildOrder(ORDER_ID);

        when(repo.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(repo.save(any(OrderEntity.class))).thenReturn(updatedOrder);

        OrderDto result = orderService.removeItemFromOrder(ORDER_ID, ITEM_ID);

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());

        verify(repo, times(1)).findById(ORDER_ID);
        verify(repo, times(1)).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when removing item from non-existent order")
    void shouldThrowExceptionWhenRemovingItemFromNonExistentOrder() {
        when(repo.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderService.removeItemFromOrder(ORDER_ID, ITEM_ID));

        assertTrue(exception.getMessage().contains(ORDER_NOT_FOUND_MESSAGE));
        verify(repo, times(1)).findById(ORDER_ID);
        verify(repo, never()).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should delete existing order successfully")
    void shouldDeleteExistingOrderSuccessfully() {
        orderService.deleteOrder(ORDER_ID);

        verify(repo, times(1)).deleteById(ORDER_ID);
    }
}

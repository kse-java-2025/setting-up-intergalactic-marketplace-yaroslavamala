package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.dto.order.OrderDto;
import java.util.List;
import java.util.UUID;

public interface OrderServiceInterface {
    OrderDto createNewOrder();
    List<OrderDto> getAllOrders();
    OrderDto getOrderById(UUID id);
    OrderDto addProductToOrder(UUID orderId, UUID productId, Integer quantity);
    OrderDto updateOrderItemQuantity(UUID orderId, UUID itemId, Integer quantity);
    OrderDto removeItemFromOrder(UUID orderId, UUID itemId);
    void deleteOrder(UUID id);
}

package com.cosmocats.cosmomarket.web;

import com.cosmocats.cosmomarket.dto.order.OrderDto;
import com.cosmocats.cosmomarket.dto.order.OrderItemCreateDto;
import com.cosmocats.cosmomarket.dto.order.OrderItemUpdateDto;
import com.cosmocats.cosmomarket.service.OrderServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderServiceInterface service;

    public OrderController(OrderServiceInterface service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder() {
        return service.createNewOrder();
    }

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return service.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable UUID id) {
        return service.getOrderById(id);
    }

    @PostMapping("/{orderId}/items")
    public OrderDto addProductToOrder(@PathVariable UUID orderId, @Valid @RequestBody OrderItemCreateDto dto) {
        return service.addProductToOrder(orderId, dto.getProductId(), dto.getQuantity());
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public OrderDto updateOrderItemQuantity(@PathVariable UUID orderId, @PathVariable UUID itemId, @Valid @RequestBody OrderItemUpdateDto dto) {
        return service.updateOrderItemQuantity(orderId, itemId, dto.getQuantity());
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public OrderDto removeItemFromOrder(@PathVariable UUID orderId, @PathVariable UUID itemId) {
        return service.removeItemFromOrder(orderId, itemId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable UUID id) {
        service.deleteOrder(id);
    }
}

package com.cosmocats.cosmomarket.service.impl;

import com.cosmocats.cosmomarket.dto.order.OrderDto;
import com.cosmocats.cosmomarket.dto.order.OrderItemDto;
import com.cosmocats.cosmomarket.exception.OrderItemNotFoundException;
import com.cosmocats.cosmomarket.exception.OrderNotFoundException;
import com.cosmocats.cosmomarket.exception.ProductNotFoundException;
import com.cosmocats.cosmomarket.repository.OrderRepository;
import com.cosmocats.cosmomarket.repository.ProductRepository;
import com.cosmocats.cosmomarket.repository.entity.OrderEntity;
import com.cosmocats.cosmomarket.repository.entity.OrderItemEntity;
import com.cosmocats.cosmomarket.repository.entity.ProductEntity;
import com.cosmocats.cosmomarket.service.OrderServiceInterface;
import com.cosmocats.cosmomarket.service.mapper.OrderMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderServiceInterface {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto createNewOrder() {
        OrderEntity toSaveOrder = OrderEntity.builder()
                .createdAt(OffsetDateTime.now())
                .totalPrice(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();
        
        OrderEntity savedOrder = orderRepo.save(toSaveOrder);
        return orderMapper.buildOrderDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderMapper.buildListOrderDto(orderRepo.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(UUID id) {
        OrderEntity order = orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        return orderMapper.buildOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto addProductToOrder(UUID orderId, UUID productId, Integer quantity) {
        OrderEntity order = orderRepo.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        ProductEntity product = productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        
        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        }
        
        OrderItemEntity newItem = new OrderItemEntity();
        newItem.setOrder(order);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        newItem.setItemPrice(product.getPrice());
        
        order.getItems().add(newItem);
        recalculateTotalPrice(order);
        
        OrderEntity savedOrder = orderRepo.save(order);
        return orderMapper.buildOrderDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto updateOrderItemQuantity(UUID orderId, UUID itemId, Integer quantity) {
        OrderEntity order = orderRepo.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        
        OrderItemEntity item = order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new OrderItemNotFoundException(itemId, orderId));
        
        item.setQuantity(quantity);
        recalculateTotalPrice(order);
        
        OrderEntity savedOrder = orderRepo.save(order);
        return orderMapper.buildOrderDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto removeItemFromOrder(UUID orderId, UUID itemId) {
        OrderEntity order = orderRepo.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.getItems().removeIf(item -> item.getId().equals(itemId));
        recalculateTotalPrice(order);
        
        OrderEntity savedOrder = orderRepo.save(order);
        return orderMapper.buildOrderDto(savedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(UUID id) {
        orderRepo.deleteById(id);
    }
    
    private void recalculateTotalPrice(OrderEntity order) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemEntity item : order.getItems()) {
            BigDecimal itemTotal = item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }
        order.setTotalPrice(totalPrice);
    }
}

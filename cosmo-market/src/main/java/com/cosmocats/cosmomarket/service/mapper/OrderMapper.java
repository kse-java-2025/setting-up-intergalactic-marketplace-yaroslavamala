package com.cosmocats.cosmomarket.service.mapper;


import com.cosmocats.cosmomarket.domain.order.Order;
import com.cosmocats.cosmomarket.domain.order.OrderItem;
import com.cosmocats.cosmomarket.dto.order.OrderDto;
import com.cosmocats.cosmomarket.dto.order.OrderItemDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface OrderMapper {

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "totalOrderPrice", ignore = true)
    OrderDto makeOrderDto(Order order);

    OrderItemDto buildOrderItemDto(OrderItem orderItem);

    @AfterMapping
    default void fillTotalOrderPrice(Order source, @MappingTarget OrderDto.OrderDtoBuilder target) {
        target.totalOrderPrice(source.totalOrderPrice());
    }
}

package com.cosmocats.cosmomarket.service.mapper;

import com.cosmocats.cosmomarket.dto.order.OrderDto;
import com.cosmocats.cosmomarket.dto.order.OrderItemDto;
import com.cosmocats.cosmomarket.repository.entity.OrderEntity;
import com.cosmocats.cosmomarket.repository.entity.OrderItemEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface OrderMapper {

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalOrderPrice", source = "totalPrice")
    OrderDto buildOrderDto(OrderEntity order);

    @Mapping(target = "product", source = "product")
    OrderItemDto buildOrderItemDto(OrderItemEntity orderItem);

    List<OrderDto> buildListOrderDto(List<OrderEntity> cart);

    @AfterMapping
    default void fillTotalOrderPrice(OrderEntity source, @MappingTarget OrderDto.OrderDtoBuilder target) {
        BigDecimal total = BigDecimal.ZERO;

        if (source.getItems() == null || source.getItems().isEmpty()) {
            target.totalOrderPrice(total);
            return;
        }

        for (OrderItemEntity item : source.getItems()) {
            BigDecimal price = item.getItemPrice();
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

            if (price != null) {
                total = total.add(price.multiply(quantity));
            }
        }

        target.totalOrderPrice(total);
    }
}

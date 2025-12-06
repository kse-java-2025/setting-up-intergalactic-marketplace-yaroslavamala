package com.cosmocats.cosmomarket.service.mapper;

import com.cosmocats.cosmomarket.dto.cart.CartDto;
import com.cosmocats.cosmomarket.dto.cart.CartItemDto;
import com.cosmocats.cosmomarket.repository.entity.CartEntity;
import com.cosmocats.cosmomarket.repository.entity.CartItemEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface CartMapper {

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "totalCartPrice", ignore = true)
    CartDto buildCartDto(CartEntity cart);

    @Mapping(target = "product", source = "product")
    CartItemDto buildCartItemDto(CartItemEntity cartItem);

    List<CartDto> buildListCartDto(List<CartEntity> cart);

    @AfterMapping
    default void calculateTotalPrice(CartEntity source, @MappingTarget CartDto.CartDtoBuilder target) {
        BigDecimal total = BigDecimal.ZERO;

        if (source.getItems() == null || source.getItems().isEmpty()) {
            target.totalCartPrice(total);
            return;
        }

        for (CartItemEntity item : source.getItems()) {
            if (item.getProduct() == null) {
                continue;
            }

            BigDecimal price = item.getProduct().getPrice();
            BigDecimal quantity = item.getQuantity() != null ? BigDecimal.valueOf(item.getQuantity()) : BigDecimal.ZERO;
            
            if (price != null) {
                BigDecimal itemTotal = price.multiply(quantity);
                total = total.add(itemTotal);
            }
        }

        target.totalCartPrice(total);
    }
}

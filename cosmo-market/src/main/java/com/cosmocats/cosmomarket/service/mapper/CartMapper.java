package com.cosmocats.cosmomarket.service.mapper;

import com.cosmocats.cosmomarket.domain.cart.Cart;
import com.cosmocats.cosmomarket.domain.cart.CartItem;
import com.cosmocats.cosmomarket.dto.cart.CartDto;
import com.cosmocats.cosmomarket.dto.cart.CartItemDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface CartMapper {

    CartDto makeCartDto(Cart cart);
    CartItemDto makeCartItemDto(CartItem cartItem);

    @AfterMapping
    default void fillTotalCartPrice(Cart source, @MappingTarget CartDto.CartDtoBuilder target) {
        target.totalCartPrice(source.totalCartPrice());
    }
}

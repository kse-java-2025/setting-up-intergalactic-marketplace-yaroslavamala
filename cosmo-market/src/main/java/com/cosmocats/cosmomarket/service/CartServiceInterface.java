package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.dto.cart.CartDto;
import java.util.List;
import java.util.UUID;

public interface CartServiceInterface {
    CartDto createNewCart();
    List<CartDto> getAllCart();
    CartDto getCartById(UUID id);
    CartDto addProductToCart(UUID cartId, UUID productId, Integer quantity);
    CartDto updateCartItemQuantity(UUID cartId, UUID itemId, Integer quantity);
    CartDto removeItemFromCart(UUID cartId, UUID itemId);
    void deleteCart(UUID id);
}

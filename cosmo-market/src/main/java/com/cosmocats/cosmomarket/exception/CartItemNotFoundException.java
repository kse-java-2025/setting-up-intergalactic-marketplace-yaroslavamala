package com.cosmocats.cosmomarket.exception;

import java.util.UUID;

public class CartItemNotFoundException extends RuntimeException {

    private static final String CART_ITEM_NOT_FOUND = "Cart item not found: '%s' in cart '%s'";

    public CartItemNotFoundException(UUID itemId, UUID cartId) {
        super(String.format(CART_ITEM_NOT_FOUND, itemId, cartId));
    }
}

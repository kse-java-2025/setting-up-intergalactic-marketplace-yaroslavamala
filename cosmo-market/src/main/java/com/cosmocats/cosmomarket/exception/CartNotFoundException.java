package com.cosmocats.cosmomarket.exception;

import java.util.UUID;

public class CartNotFoundException extends RuntimeException {

    private static final String CART_NOT_FOUND = "Cart not found: %s";

    public CartNotFoundException(UUID cartId) {
        super(String.format(CART_NOT_FOUND, cartId));
    }
}

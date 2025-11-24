package com.cosmocats.cosmomarket.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Product not found: ";

    public ProductNotFoundException(UUID productId) {
        super(MESSAGE_TEMPLATE + productId);
    }
}

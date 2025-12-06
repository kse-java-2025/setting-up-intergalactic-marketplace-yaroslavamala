package com.cosmocats.cosmomarket.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Product not found: %s";

    public ProductNotFoundException(UUID productId) {
        super(String.format(MESSAGE_TEMPLATE, productId));
    }
}

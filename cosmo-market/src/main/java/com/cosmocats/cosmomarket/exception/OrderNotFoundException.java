package com.cosmocats.cosmomarket.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    private static final String ORDER_NOT_FOUND = "Order not found: %s";

    public OrderNotFoundException(UUID orderId) {
        super(String.format(ORDER_NOT_FOUND, orderId));
    }
}

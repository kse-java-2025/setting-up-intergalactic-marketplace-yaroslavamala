package com.cosmocats.cosmomarket.exception;

import java.util.UUID;

public class OrderItemNotFoundException extends RuntimeException {

    private static final String ORDER_ITEM_NOT_FOUND = "Order item not found: '%s' in order '%s'";

    public OrderItemNotFoundException(UUID itemId, UUID orderId) {
        super(String.format(ORDER_ITEM_NOT_FOUND, itemId, orderId));
    }
}

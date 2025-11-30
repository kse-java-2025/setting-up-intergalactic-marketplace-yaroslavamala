package com.cosmocats.cosmomarket.domain.cart;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Cart {
    @Builder.Default
    UUID id = UUID.randomUUID();

    @Singular("item")
    List<CartItem> items;

    public BigDecimal totalCartPrice() {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;

        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item != null) {
                BigDecimal price = item.totalItemPrice();
                if (price != null) sum = sum.add(price);
            }
        }
        return sum;
    }
}
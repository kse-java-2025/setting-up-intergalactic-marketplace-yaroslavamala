package com.cosmocats.cosmomarket.domain.cart;

import lombok.Builder;
import lombok.Value;
import com.cosmocats.cosmomarket.domain.product.Product;
import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class CartItem {
    Product product;
    Integer quantity;

    public BigDecimal totalItemPrice() {
        if (product == null || product.getPrice() == null) return BigDecimal.ZERO;
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}

package com.cosmocats.cosmomarket.domain.order;


import com.cosmocats.cosmomarket.domain.product.Product;
import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class OrderItem {
    Product product;
    Integer quantity;
    BigDecimal itemPrice;
}

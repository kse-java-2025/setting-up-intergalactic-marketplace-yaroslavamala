package com.cosmocats.cosmomarket.domain.product;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Product {
    UUID id;
    String name;
    String description;
    Long categoryId;
    Integer availableQuantity;
    BigDecimal price;
}

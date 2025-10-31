package com.cosmocats.cosmomarket.dto.product;

import com.cosmocats.cosmomarket.domain.category.Category;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
@Jacksonized
public class ProductReturnDto {
    UUID id;
    String name;
    String description;
    Category category;
    Integer availableQuantity;
    BigDecimal price;
}

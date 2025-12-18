package com.cosmocats.cosmomarket.dto.order;

import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
@Jacksonized
public class OrderItemDto {
    UUID id;
    ProductReturnDto product;
    Integer quantity;
    BigDecimal itemPrice;
}


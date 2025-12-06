package com.cosmocats.cosmomarket.dto.cart;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
@Jacksonized
public class CartDto {
    UUID id;
    OffsetDateTime createdAt;

    @Singular("item")
    List<CartItemDto> items;

    BigDecimal totalCartPrice;
}
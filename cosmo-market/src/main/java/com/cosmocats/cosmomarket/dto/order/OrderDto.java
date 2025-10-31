package com.cosmocats.cosmomarket.dto.order;

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
public class OrderDto {
    UUID id;
    OffsetDateTime createdAt;

    @Singular("item")
    List<OrderItemDto> items;

    BigDecimal totalOrderPrice;
}

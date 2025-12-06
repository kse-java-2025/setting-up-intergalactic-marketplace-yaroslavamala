package com.cosmocats.cosmomarket.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.UUID;

@Value
@Builder
@Jacksonized
public class CartItemCreateDto {
    
    @NotNull(message = "productId is required")
    UUID productId;
    
    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be >= 1")
    Integer quantity;
}

package com.cosmocats.cosmomarket.dto.cart;

import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.UUID;

@Value
@Builder
@Jacksonized
public class CartItemDto {
    UUID id;
    ProductReturnDto product;
    Integer quantity;
}

package com.cosmocats.cosmomarket.dto.cart;

import com.cosmocats.cosmomarket.dto.product.ProductReturnDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CartItemDto {
    ProductReturnDto product;
    Integer quantity;
}

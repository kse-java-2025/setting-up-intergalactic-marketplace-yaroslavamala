package com.cosmocats.cosmomarket.dto.product;

import com.cosmocats.cosmomarket.validation.CosmicWordCheck;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProductUpdateDto {

    @CosmicWordCheck
    String name;

    @Size(max = 255)
    String description;

    Long categoryId;

    @Min(0)
    Integer availableQuantity;

    @DecimalMin("0.01")
    BigDecimal price;
}

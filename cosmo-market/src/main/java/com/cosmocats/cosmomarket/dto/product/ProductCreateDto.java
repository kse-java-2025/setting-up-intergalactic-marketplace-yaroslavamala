package com.cosmocats.cosmomarket.dto.product;

import com.cosmocats.cosmomarket.domain.category.Category;
import com.cosmocats.cosmomarket.validation.CosmicWordCheck;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;

@Value
@Builder
@Jacksonized
public class ProductCreateDto {

    @NotBlank(message = "name is required")
    @CosmicWordCheck
    String name;

    @Size(max = 255, message = "description is max 255 chars")
    String description;

    @NotNull(message = "category is required, must be one of food/clothes/accessory/medical/other")
    Category category;

    @NotNull
    @Min(value = 0, message = "available quantity must be >= 0")
    Integer availableQuantity;

    @NotNull
    @DecimalMin(value = "0.01", message = "price must be >= 0.01")
    BigDecimal price;
}

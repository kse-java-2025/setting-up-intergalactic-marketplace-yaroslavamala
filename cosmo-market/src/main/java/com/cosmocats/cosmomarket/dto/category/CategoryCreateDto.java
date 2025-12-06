package com.cosmocats.cosmomarket.dto.category;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CategoryCreateDto {
    @NotNull(message = "name is required")
    String name;
}

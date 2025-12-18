package com.cosmocats.cosmomarket.dto.category;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CategoryReturnDto {
    Long id;
    String name;
}

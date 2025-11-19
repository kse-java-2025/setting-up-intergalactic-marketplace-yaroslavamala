package com.cosmocats.cosmomarket.domain.cosmocat;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class CosmoCat {
    UUID id;
    String name;
    String description;
    String planet;
    Integer years;
}

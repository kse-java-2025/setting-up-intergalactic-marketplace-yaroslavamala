package com.cosmocats.cosmomarket.service.impl;

import com.cosmocats.cosmomarket.domain.cosmocat.CosmoCat;
import com.cosmocats.cosmomarket.featuretoggle.FeatureToggles;
import com.cosmocats.cosmomarket.featuretoggle.annotation.FeatureToggle;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CosmoCatService {

    private final List<CosmoCat> cosmoCats = List.of(
            CosmoCat.builder()
                    .id(UUID.randomUUID())
                    .name("Capitan Meowkins")
                    .description("Expert in in his work, for sure")
                    .planet("Meowturn")
                    .years(5)
                    .build(),

            CosmoCat.builder()
                    .id(UUID.randomUUID())
                    .name("Admiral Kitsunya")
                    .description("Beautiful and very smart cat")
                    .planet("Purina")
                    .years(10)
                    .build(),

            CosmoCat.builder()
                    .id(UUID.randomUUID())
                    .name("Commander Fluffykins")
                    .description("Know technology better then people")
                    .planet("Cat-urn")
                    .years(6)
                    .build()
    );


    @FeatureToggle(FeatureToggles.COSMO_CATS)
    public List<CosmoCat> getCosmoCats() {
        return cosmoCats;
    }
}

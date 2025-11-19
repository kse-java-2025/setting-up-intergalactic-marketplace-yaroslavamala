package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.domain.cosmocat.CosmoCat;
import com.cosmocats.cosmomarket.featuretoggle.FeatureToggleService;
import com.cosmocats.cosmomarket.featuretoggle.FeatureToggles;
import com.cosmocats.cosmomarket.featuretoggle.exception.FeatureNotAvailableException;
import com.cosmocats.cosmomarket.service.impl.CosmoCatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DisplayName("Cosmo Cat Service Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CosmoCatServiceTest {

    private static final String COSMO_CATS_FEATURE_NAME = FeatureToggles.COSMO_CATS.getFeatureName();

    @Autowired
    private CosmoCatService cosmoCatService;

    @Autowired
    private FeatureToggleService featureToggleService;

    @Test
    @Order(1)
    @DisplayName("getCosmoCats() should return list of cats successfully when feature enabled")
    void shouldReturnCosmoCatsWhenFeatureIsEnabled() {
        featureToggleService.enable(COSMO_CATS_FEATURE_NAME);

        List<CosmoCat> result = cosmoCatService.getCosmoCats();

        assertNotNull(result);
        assertEquals(3, result.size());
        result.forEach(cat -> assertNotNull(cat.getId()));
    }

    @Test
    @Order(2)
    @DisplayName("getCosmoCats() should throw exception when feature disabled")
    void shouldThrowExceptionWhenFeatureIsDisabled() {
        featureToggleService.disable(COSMO_CATS_FEATURE_NAME);

        FeatureNotAvailableException exception = assertThrows(FeatureNotAvailableException.class, () -> cosmoCatService.getCosmoCats());
        
        assertNotNull(exception.getMessage());
    }
}

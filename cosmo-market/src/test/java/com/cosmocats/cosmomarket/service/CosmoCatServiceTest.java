package com.cosmocats.cosmomarket.service;

import com.cosmocats.cosmomarket.AbstractIT;
import com.cosmocats.cosmomarket.domain.cosmocat.CosmoCat;
import com.cosmocats.cosmomarket.featuretoggle.FeatureToggleService;
import com.cosmocats.cosmomarket.featuretoggle.FeatureToggles;
import com.cosmocats.cosmomarket.featuretoggle.exception.FeatureNotAvailableException;
import com.cosmocats.cosmomarket.service.impl.CosmoCatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Cosmo Cat Service Tests")
class CosmoCatServiceTest extends AbstractIT {

    private static final String COSMO_CATS_FEATURE_NAME = FeatureToggles.COSMO_CATS.getFeatureName();

    @Autowired
    private CosmoCatService cosmoCatService;

    @MockitoSpyBean
    private FeatureToggleService featureToggleService;

    @Test
    @DisplayName("getCosmoCats() should return list of cats successfully when feature enabled")
    void shouldReturnCosmoCatsWhenFeatureIsEnabled() {
        when(featureToggleService.check(COSMO_CATS_FEATURE_NAME)).thenReturn(true);

        List<CosmoCat> result = cosmoCatService.getCosmoCats();

        assertNotNull(result);
        assertEquals(3, result.size());
        result.forEach(cat -> assertNotNull(cat.getId()));
    }

    @Test
    @DisplayName("getCosmoCats() should throw exception when feature disabled")
    void shouldThrowExceptionWhenFeatureIsDisabled() {
        when(featureToggleService.check(COSMO_CATS_FEATURE_NAME)).thenReturn(false);

        FeatureNotAvailableException exception = assertThrows(FeatureNotAvailableException.class, () -> cosmoCatService.getCosmoCats());
        
        assertNotNull(exception.getMessage());
    }
}

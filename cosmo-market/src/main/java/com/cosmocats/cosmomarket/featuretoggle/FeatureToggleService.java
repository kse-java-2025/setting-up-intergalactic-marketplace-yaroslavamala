package com.cosmocats.cosmomarket.featuretoggle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

    @Value("${feature.cosmoCats.enabled:false}")
    private boolean cosmoCatsEnabled;
    private final String FEATURE_ONE = "cosmoCats";

    @Value("${feature.kittyProducts.enabled:false}")
    private boolean kittyProductsEnabled;
    protected final String FEATURE_TWO = "kittyProducts";

    public boolean check(String featureName) {
        return switch (featureName) {
            case FEATURE_ONE -> cosmoCatsEnabled;
            case FEATURE_TWO -> kittyProductsEnabled;
            default -> false;
        };
    }

    public void enable(String featureName) {
        switch (featureName) {
            case FEATURE_ONE -> cosmoCatsEnabled = true;
            case FEATURE_TWO -> kittyProductsEnabled = true;
        }
    }

    public void disable(String featureName) {
        switch (featureName) {
            case FEATURE_ONE -> cosmoCatsEnabled = false;
            case FEATURE_TWO -> kittyProductsEnabled = false;
        }
    }
}

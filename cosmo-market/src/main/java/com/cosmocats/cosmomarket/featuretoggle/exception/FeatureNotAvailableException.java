package com.cosmocats.cosmomarket.featuretoggle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FeatureNotAvailableException extends RuntimeException {

    private static final String FEATURE_TOGGLE_NOT_ENABLED = "Feature toggle %s is not enabled";

    public FeatureNotAvailableException(String featureToggleName) {
        super(String.format(FEATURE_TOGGLE_NOT_ENABLED, featureToggleName));
    }

}
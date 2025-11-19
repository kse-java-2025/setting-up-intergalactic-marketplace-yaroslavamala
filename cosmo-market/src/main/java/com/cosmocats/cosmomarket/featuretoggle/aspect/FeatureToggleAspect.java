package com.cosmocats.cosmomarket.featuretoggle.aspect;

import com.cosmocats.cosmomarket.featuretoggle.FeatureToggleService;
import com.cosmocats.cosmomarket.featuretoggle.FeatureToggles;
import com.cosmocats.cosmomarket.featuretoggle.annotation.FeatureToggle;
import com.cosmocats.cosmomarket.featuretoggle.exception.FeatureNotAvailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FeatureToggleAspect {

    private final FeatureToggleService featureToggleService;

    @Around(value = "@annotation(featureToggle)")
    public Object checkFeatureToggleAnnotation(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle) throws Throwable {
        return checkToggle(joinPoint, featureToggle);
    }

    private Object checkToggle(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle) throws Throwable {
        FeatureToggles toggle = featureToggle.value();

        if (featureToggleService.check(toggle.getFeatureName())) {
            return joinPoint.proceed();
        }

        throw new FeatureNotAvailableException(toggle.getFeatureName());
    }
}

package com.cosmocats.cosmomarket.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CosmicWordValidator implements ConstraintValidator<CosmicWordCheck, String> {

    private static final List<String> COSMIC_TERMS = List.of("star", "galaxy", "comet", "cosmo", "cosmic", "space", "asteroid");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String lower = value.toLowerCase();
        for (String term : COSMIC_TERMS) {
            if (lower.contains(term)) {
                return true;
            }
        }

        return false;
    }
}

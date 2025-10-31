package com.cosmocats.cosmomarket.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = CosmicWordValidator.class)
@Documented
public @interface CosmicWordCheck {

    String COSMIC_NAME_SHOULD_BE_VALID = "\"Invalid Cosmic Name: The provided name does not conform to the required format. " +
            "Please ensure that it includes at least one of cosmic word. Example: 'Galaxy Pizza'.\"\n";

    String message() default COSMIC_NAME_SHOULD_BE_VALID;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

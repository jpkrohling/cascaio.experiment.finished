package com.cascaio.backend.v1.control.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * User: jpkrohling
 * Date: 7/2/13
 * Time: 8:22 AM
 */
@Documented
@Constraint(validatedBy = CountryValidator.class)
@Target( { METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Country {
	String message() default "{com.cascaio.backend.v1.control.validation.Country.invalidCountry}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}

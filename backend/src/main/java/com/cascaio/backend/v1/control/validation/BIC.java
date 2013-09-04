package com.cascaio.backend.v1.control.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 7:50 PM
 */
@Documented
@Constraint(validatedBy = BICValidator.class)
@Target( { METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface BIC {
	String message() default "{com.cascaio.backend.v1.control.validation.BIC.invalidBIC}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}

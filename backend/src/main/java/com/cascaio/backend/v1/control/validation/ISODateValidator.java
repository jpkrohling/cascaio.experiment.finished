package com.cascaio.backend.v1.control.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 7:27 PM
 */
public class ISODateValidator implements ConstraintValidator<ISODate, String> {
	@Override
	public void initialize(ISODate constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null
				|| value.isEmpty()
				|| value.matches("^\\d{4}-((0[1-9])|(1[012]))-((0[1-9]|[12]\\d)|3[01])$");
	}
}

package com.cascaio.backend.v1.control.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 8:04 PM
 */
public class NumericRateValidator implements ConstraintValidator<NumericRate, String> {
	@Override
	public void initialize(NumericRate constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null
				|| value.isEmpty()
				|| value.matches("^\\d+(\\.\\d{1,6})?$");
	}
}

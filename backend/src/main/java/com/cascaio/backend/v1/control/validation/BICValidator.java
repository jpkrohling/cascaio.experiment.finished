package com.cascaio.backend.v1.control.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 7:51 PM
 */
public class BICValidator implements ConstraintValidator<BIC, String> {
	@Override
	public void initialize(BIC constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null
				|| value.isEmpty()
				||  value.matches("^\\w{11}$");
	}
}

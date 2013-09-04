package com.cascaio.backend.v1.control.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 7:37 PM
 */
public class BankleitzahlValidator implements ConstraintValidator<Bankleitzahl, String>  {
	@Override
	public void initialize(Bankleitzahl constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null
				|| value.isEmpty()
				|| value.matches("^\\d{8}$");
	}
}

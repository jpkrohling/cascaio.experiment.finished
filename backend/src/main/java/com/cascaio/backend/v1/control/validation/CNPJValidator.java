package com.cascaio.backend.v1.control.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 7:53 PM
 */
public class CNPJValidator implements ConstraintValidator<CNPJ, String> {
	@Override
	public void initialize(CNPJ constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null
				|| value.isEmpty()
				||  value.matches("/^[0-9]{8}$/");
	}
}

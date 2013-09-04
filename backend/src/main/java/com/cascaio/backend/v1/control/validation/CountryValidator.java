package com.cascaio.backend.v1.control.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

/**
 * User: jpkrohling
 * Date: 7/2/13
 * Time: 8:22 AM
 */
public class CountryValidator implements ConstraintValidator<Country, String> {
	@Override
	public void initialize(Country constraintAnnotation) {
	}

	@Override
	public boolean isValid(String country, ConstraintValidatorContext context) {
		boolean validCountry = false;

		if (null == country || country.isEmpty()) {
			return true;
		}

		for (Locale locale : Locale.getAvailableLocales()) {
			if (locale.getCountry().equalsIgnoreCase(country)) {
				validCountry = true;
			}
		}

		return validCountry;
	}
}

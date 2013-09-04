package com.cascaio.backend.v1.control.validation;

import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 7:21 PM
 */
public class CurrencyValidator implements ConstraintValidator<Currency, String> {
	Logger logger = LoggerFactory.getLogger(CurrencyValidator.class);

	@Override
	public void initialize(Currency constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			logger.trace("Got an empty currency. Validation passed.");
			return true;
		}

		for (CurrencyUnit currency : CurrencyUnit.registeredCurrencies()) {
			if (currency.getCurrencyCode().equals(value)) {
				logger.trace("Found {} in the currency stack", value);
				return true;
			}

		}

		logger.trace("Couldn't find {} in the currency stack", value);
		return false;
	}
}

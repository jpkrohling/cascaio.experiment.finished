package com.cascaio.backend.v1.entity;

import org.joda.money.CurrencyUnit;

import javax.persistence.Entity;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:42 PM
 */
@Entity
public class CreditCardAccount extends MoneyAccount {
	protected CreditCardAccount() {
	}

	public CreditCardAccount(CascaioUser user, String name, CurrencyUnit currency) {
		super(user, name, currency);
	}

	public CreditCardAccount(String id, CascaioUser user, String name, CurrencyUnit currency) {
		super(id, user, name, currency);
	}
}

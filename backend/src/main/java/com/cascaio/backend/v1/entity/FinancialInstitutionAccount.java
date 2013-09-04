package com.cascaio.backend.v1.entity;

import org.joda.money.CurrencyUnit;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 5:19 PM
 */
@Entity
public abstract class FinancialInstitutionAccount extends MoneyAccount {
	@ManyToOne
	@NotNull
	private FinancialInstitution financialInstitution;

	// JPA happy
	protected FinancialInstitutionAccount() {
	}

	public FinancialInstitutionAccount(CascaioUser user, String name, CurrencyUnit currency, FinancialInstitution financialInstitution) {
		super(user, name, currency);
		this.financialInstitution = financialInstitution;
	}

	public FinancialInstitutionAccount(String id, CascaioUser user, String name, CurrencyUnit currency, FinancialInstitution financialInstitution) {
		super(id, user, name, currency);
		this.financialInstitution = financialInstitution;
	}

	public void setFinancialInstitution(FinancialInstitution financialInstitution) {
		this.financialInstitution = financialInstitution;
	}

	public FinancialInstitution getFinancialInstitution() {
		return financialInstitution;
	}

}

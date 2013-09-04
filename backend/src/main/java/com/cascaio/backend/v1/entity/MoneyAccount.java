package com.cascaio.backend.v1.entity;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.joda.money.CurrencyUnit;
import org.joda.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 5:22 PM
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class MoneyAccount extends Account {

	@Type(type = "CurrencyUnit")
	@NotNull
	private CurrencyUnit currency;

	@Formula("( select sum(mts.amount) from MoneyTransaction mts where mts.account_id = id )")
	private BigDecimal total;

	// JPA happy
	protected MoneyAccount() {
		super();
	}

	protected MoneyAccount(CascaioUser user, String name, CurrencyUnit currency) {
		super(user, name);
		this.currency = currency;
	}

	protected MoneyAccount(String id, CascaioUser user, String name, CurrencyUnit currency) {
		super(id, user, name);
		this.currency = currency;
	}

	public void setCurrency(CurrencyUnit currency) {
		this.currency = currency;
	}

	public CurrencyUnit getCurrency() {
		return currency;
	}

	public Transaction addTransaction(String name, LocalDate date, BigDecimal amount) {
		Transaction transaction = new MoneyTransaction(name, date, amount, this);
		this.transactions.add(transaction);
		return transaction;
	}

	@Override
	public BigDecimal getTotal() {
		return total;
	}
}

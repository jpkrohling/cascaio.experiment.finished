package com.cascaio.backend.v1.entity;

import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * User: jpkrohling
 * Date: 7/7/13
 * Time: 4:49 PM
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"currencyFrom", "currencyTo", "date"})})
public class ExchangeRate extends Quote {

	@NotNull
	private CurrencyUnit currencyFrom;

	@NotNull
	private CurrencyUnit currencyTo;

	public ExchangeRate(CurrencyUnit currencyFrom, CurrencyUnit currencyTo, BigDecimal rate, DateTime date) {
		super(date, rate);
		this.currencyFrom = currencyFrom;
		this.currencyTo = currencyTo;
	}

	// JPA Happy
	protected ExchangeRate() {
	}

	public CurrencyUnit getCurrencyFrom() {
		return currencyFrom;
	}

	public void setCurrencyFrom(CurrencyUnit currencyFrom) {
		this.currencyFrom = currencyFrom;
	}

	public CurrencyUnit getCurrencyTo() {
		return currencyTo;
	}

	public void setCurrencyTo(CurrencyUnit currencyTo) {
		this.currencyTo = currencyTo;
	}

	@Override
	public String toString() {
		return "ExchangeRate{" +
				"currencyFrom=" + currencyFrom +
				", currencyTo=" + currencyTo +
				", rate=" + this.getPrice() +
				", date=" + this.getDate() +
				'}';
	}
}

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
public class ExchangeRate extends CascaioEntity {

	@NotNull
	private CurrencyUnit currencyFrom;

	@NotNull
	private CurrencyUnit currencyTo;

	@NotNull
	@Column(precision = 19, scale = 6)
	private BigDecimal rate;

	@NotNull
	private DateTime date;

	public ExchangeRate(CurrencyUnit currencyFrom, CurrencyUnit currencyTo, BigDecimal rate, DateTime date) {
		this.currencyFrom = currencyFrom;
		this.currencyTo = currencyTo;
		this.rate = rate;
		this.date = date;
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

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "ExchangeRate{" +
				"currencyFrom=" + currencyFrom +
				", currencyTo=" + currencyTo +
				", rate=" + rate +
				", date=" + date +
				'}';
	}
}

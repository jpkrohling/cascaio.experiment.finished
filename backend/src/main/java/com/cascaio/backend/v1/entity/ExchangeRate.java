package com.cascaio.backend.v1.entity;

import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;

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

	public CurrencyUnit getCurrencyTo() {
		return currencyTo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ExchangeRate)) return false;
		if (!super.equals(o)) return false;

		ExchangeRate that = (ExchangeRate) o;

		if (currencyFrom != null ? !currencyFrom.equals(that.currencyFrom) : that.currencyFrom != null) return false;
		if (currencyTo != null ? !currencyTo.equals(that.currencyTo) : that.currencyTo != null) return false;

		return super.equals(o);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (currencyFrom != null ? currencyFrom.hashCode() : 0);
		result = 31 * result + (currencyTo != null ? currencyTo.hashCode() : 0);
		result = 31 * super.hashCode();
		return result;
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

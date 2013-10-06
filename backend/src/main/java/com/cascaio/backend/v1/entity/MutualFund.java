package com.cascaio.backend.v1.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:45 PM
 */
@Entity
public class MutualFund extends NamedCascaioEntity {

	@NotNull
	@Column(unique = true, length = 12)
	private String isin;

	@Column(length = 6)
	private String wkn;

	@NotNull
	@Column(length = 3)
	private CurrencyUnit currency;

	@OneToMany(mappedBy = "mutualFund", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private List<MutualFundQuote> quotes = new ArrayList<>();

	protected MutualFund() {
	}

	public MutualFund(String name, String isin, CurrencyUnit currency) {
		this(UUID.randomUUID().toString(), name, isin, currency);
	}

	public MutualFund(String id, String name, String isin, CurrencyUnit currency) {
		super(id, name);
		this.isin = isin;
		this.currency = currency;
	}

	public List<MutualFundQuote> getQuotes() {
		return Collections.unmodifiableList(quotes);
	}

	public MutualFundQuote addQuote(DateTime date, BigDecimal price) {
		MutualFundQuote quote = new MutualFundQuote(date, price, this);
		this.quotes.add(quote);
		return quote;
	}

	public String getIsin() {
		return isin;
	}

	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	public CurrencyUnit getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyUnit currency) {
		this.currency = currency;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MutualFund)) return false;

		MutualFund fund = (MutualFund) o;

		//if (currency != null ? !currency.equals(fund.currency) : fund.currency != null) return false;
		if (isin != null ? !isin.equals(fund.isin) : fund.isin != null) return false;
		if (wkn != null ? !wkn.equals(fund.wkn) : fund.wkn != null) return false;
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		int result = isin != null ? isin.hashCode() : 0;
		result = 31 * result + (wkn != null ? wkn.hashCode() : 0);
		result = 31 * result + (currency != null ? currency.hashCode() : 0);
		result = 31 * result + (quotes != null ? quotes.hashCode() : 0);
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "MutualFund{" +
				"isin='" + isin + '\'' +
				", wkn='" + wkn + '\'' +
				", currency=" + currency +
				", named=" + super.toString() +
				'}';
	}
}

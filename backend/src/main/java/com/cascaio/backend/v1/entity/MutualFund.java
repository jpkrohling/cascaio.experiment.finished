package com.cascaio.backend.v1.entity;

import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
}

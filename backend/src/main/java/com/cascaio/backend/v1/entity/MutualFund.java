package com.cascaio.backend.v1.entity;

import org.joda.money.CurrencyUnit;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:45 PM
 */
@Entity
public class MutualFund extends NamedCascaioEntity {

	@Column(unique = true, length = 12)
	private String isin;

	@Column(length = 6)
	private String wkn;

	@Column(length = 3)
	private CurrencyUnit currency;

	@OneToMany(mappedBy = "mutualFund", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MutualFundQuote> quotes = new ArrayList<>();

	protected MutualFund() {
	}

	public MutualFund(String name, CurrencyUnit currency) {
		super(name);
		this.currency = currency;
	}

	public MutualFund(String id, String name, CurrencyUnit currency) {
		super(id, name);
		this.currency = currency;
	}

	public List<MutualFundQuote> getQuotes() {
		return Collections.unmodifiableList(quotes);
	}

	public void addQuote(LocalDate date, BigDecimal price) {
		this.quotes.add(new MutualFundQuote(date, price, this));
	}
}

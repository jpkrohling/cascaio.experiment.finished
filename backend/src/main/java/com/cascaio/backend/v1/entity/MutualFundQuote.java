package com.cascaio.backend.v1.entity;

import org.joda.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:47 PM
 */
@Entity
public class MutualFundQuote extends Quote {

	@ManyToOne
	private MutualFund mutualFund;

	// JPA happy
	protected MutualFundQuote() {
	}

	public MutualFundQuote(LocalDate date, BigDecimal price, MutualFund mutualFund) {
		super(date, price);
		this.mutualFund = mutualFund;
	}

	public MutualFundQuote(String id, LocalDate date, BigDecimal price, MutualFund mutualFund) {
		super(id, date, price);
		this.mutualFund = mutualFund;
	}

	public MutualFund getMutualFund() {
		return mutualFund;
	}
}

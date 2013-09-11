package com.cascaio.backend.v1.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:47 PM
 */
@Entity
public class MutualFundQuote extends Quote {

	@ManyToOne
	@JsonIgnore
	private MutualFund mutualFund;

	// JPA happy
	protected MutualFundQuote() {
	}

	protected MutualFundQuote(DateTime date, BigDecimal price, MutualFund mutualFund) {
		this(UUID.randomUUID().toString(), date, price, mutualFund);
	}

	protected MutualFundQuote(String id, DateTime date, BigDecimal price, MutualFund mutualFund) {
		super(id, date, price);
		this.mutualFund = mutualFund;
	}

	public MutualFund getMutualFund() {
		return mutualFund;
	}
}

package com.cascaio.backend.v1.entity;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:46 PM
 */
@MappedSuperclass
public class Quote extends CascaioEntity {

	@NotNull
	private DateTime date;

	@NotNull
	@Column(precision = 19, scale = 6)
	private BigDecimal price;

	// JPA happy
	protected Quote() {
	}

	public Quote(DateTime date, BigDecimal price) {
		this.date = date;
		this.price = price;
	}

	public Quote(String id, DateTime date, BigDecimal price) {
		super(id);
		this.date = date;
		this.price = price;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}
}

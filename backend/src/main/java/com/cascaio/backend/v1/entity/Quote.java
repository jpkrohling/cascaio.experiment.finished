package com.cascaio.backend.v1.entity;

import org.joda.time.LocalDate;

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
	private LocalDate date;

	@NotNull
	private BigDecimal price;

	// JPA happy
	protected Quote() {
	}

	public Quote(LocalDate date, BigDecimal price) {
		this.date = date;
		this.price = price;
	}

	public Quote(String id, LocalDate date, BigDecimal price) {
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
}

package com.cascaio.backend.v1.entity;

import org.joda.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 5:24 PM
 */
@Entity
public class MoneyTransaction extends Transaction {

	@ManyToOne
	@NotNull
	private Category category;

	// JPA happy
	protected MoneyTransaction() {
	}

	MoneyTransaction(String name, LocalDate date, BigDecimal amount, MoneyAccount account) {
		super(name, date, amount, account);
	}

	MoneyTransaction(String id, String name, LocalDate date, BigDecimal amount, MoneyAccount account) {
		super(id, name, date, amount, account);
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}

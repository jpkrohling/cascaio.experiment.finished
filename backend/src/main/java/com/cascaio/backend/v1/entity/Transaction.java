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
public abstract class Transaction extends NamedCascaioEntity {

	@NotNull
	private LocalDate date;

	@NotNull
	private BigDecimal amount;

	@ManyToOne
	@NotNull
	private Account account;

	// JPA happy
	protected Transaction() {
	}

	public Transaction(String name, LocalDate date, BigDecimal amount, Account account) {
		super(name);
		this.date = date;
		this.amount = amount;
		this.account = account;
	}

	public Transaction(String id, String name, LocalDate date, BigDecimal amount, Account account) {
		super(id, name);
		this.date = date;
		this.amount = amount;
		this.account = account;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Account getAccount() {
		return account;
	}
}

package com.cascaio.backend.v1.entity;

import org.joda.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 5:23 PM
 */
@Entity
public abstract class Account extends NamedUserData {
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	protected List<Transaction> transactions = new ArrayList<>();

	protected Account() {
	}

	protected Account(CascaioUser user, String name) {
		super(user, name);
	}

	protected Account(String id, CascaioUser user, String name) {
		super(id, user, name);
	}

	public List<Transaction> getTransactions() {
		return Collections.unmodifiableList(transactions);
	}

	public abstract Transaction addTransaction(String name, LocalDate date, BigDecimal amount);

	public abstract BigDecimal getTotal();
}

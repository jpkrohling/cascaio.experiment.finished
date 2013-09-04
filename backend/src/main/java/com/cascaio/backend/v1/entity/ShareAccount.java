package com.cascaio.backend.v1.entity;

import org.hibernate.annotations.Formula;
import org.joda.time.LocalDate;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:43 PM
 */
@Entity
public class ShareAccount extends Account {

	@Formula("( select sum(sts.amount) from ShareTransaction sts where sts.account_id = id )")
	private BigDecimal total;

	// JPA happy
	protected ShareAccount() {
	}

	public ShareAccount(CascaioUser user, String name) {
		super(user, name);
	}

	public ShareAccount(String id, CascaioUser user, String name) {
		super(id, user, name);
	}

	public Transaction addTransaction(String name, LocalDate date, BigDecimal amount) {
		Transaction transaction = new ShareTransaction(name, date, amount, this);
		this.transactions.add(transaction);
		return transaction;
	}

	@Override
	public BigDecimal getTotal() {
		return total;
	}
}

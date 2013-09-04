package com.cascaio.backend.v1.entity;

import org.joda.time.LocalDate;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:44 PM
 */
@Entity
public class ShareTransaction extends Transaction {

	// JPA happy
	protected ShareTransaction() {
	}

	ShareTransaction(String name, LocalDate date, BigDecimal amount, ShareAccount account) {
		super(name, date, amount, account);
	}

	ShareTransaction(String id, String name, LocalDate date, BigDecimal amount, ShareAccount account) {
		super(id, name, date, amount, account);
	}

}

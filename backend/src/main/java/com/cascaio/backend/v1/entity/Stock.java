package com.cascaio.backend.v1.entity;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:53 PM
 */
@Entity
public class Stock extends CascaioEntity {

	@NotNull
	private String symbol;

	@NotNull
	private String market;

	// JPA happy
	protected Stock() {
	}

	public Stock(String symbol, String market) {
		this.symbol = symbol;
		this.market = market;
	}

	public Stock(String id, String symbol, String market) {
		super(id);
		this.symbol = symbol;
		this.market = market;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getSymbol() {
		return symbol;
	}
}

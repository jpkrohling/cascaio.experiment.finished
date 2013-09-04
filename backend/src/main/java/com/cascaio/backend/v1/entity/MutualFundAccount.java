package com.cascaio.backend.v1.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 7:44 PM
 */
@Entity
public class MutualFundAccount extends ShareAccount {

	@ManyToOne
	@NotNull
	private MutualFund mutualFund;

	// JPA happy
	protected MutualFundAccount() {
	}

	public MutualFundAccount(CascaioUser user, String name, MutualFund mutualFund) {
		super(user, name);
		this.mutualFund = mutualFund;
	}

	public MutualFundAccount(String id, CascaioUser user, String name, MutualFund mutualFund) {
		super(id, user, name);
		this.mutualFund = mutualFund;
	}

	public MutualFund getMutualFund() {
		return mutualFund;
	}
}

package com.cascaio.backend.v1.entity;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 12:29 PM
 */
@MappedSuperclass
public abstract class UserData extends CascaioEntity {
	@ManyToOne
	private CascaioUser user;

	protected UserData() {
	}

	public UserData(CascaioUser user) {
		this.user = user;
	}

	public UserData(String id, CascaioUser user) {
		super(id);
		this.user = user;
	}

	public CascaioUser getUser() {
		return user;
	}
}

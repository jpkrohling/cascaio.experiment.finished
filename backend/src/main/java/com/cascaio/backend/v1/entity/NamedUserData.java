package com.cascaio.backend.v1.entity;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 12:31 PM
 */
@MappedSuperclass
public class NamedUserData extends UserData {

	@NotNull
	private String name;

	// JPA happy
	protected NamedUserData() {
	}

	public NamedUserData(CascaioUser user, String name) {
		super(user);
		this.name = name;
	}

	public NamedUserData(String id, CascaioUser user, String name) {
		super(id, user);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

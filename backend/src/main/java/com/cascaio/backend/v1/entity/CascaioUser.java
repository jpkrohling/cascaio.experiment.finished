package com.cascaio.backend.v1.entity;

import javax.persistence.Entity;

/**
 * User: jpkrohling
 * Date: 6/30/13
 * Time: 12:29 PM
 */
@Entity
public class CascaioUser extends CascaioEntity {

	protected CascaioUser() {
	}

	public CascaioUser(String id) {
		super(id);
	}
}

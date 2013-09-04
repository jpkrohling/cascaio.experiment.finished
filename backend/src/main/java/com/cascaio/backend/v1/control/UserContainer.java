package com.cascaio.backend.v1.control;

import com.cascaio.backend.v1.entity.CascaioUser;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 12:22 PM
 */
@RequestScoped
public class UserContainer {
	private CascaioUser user;

	@Inject
	EntityManager entityManager;

	@Produces
	@CurrentUser
	public CascaioUser getUser() {
		return user;
	}

	public void setUser(String userId) {
		// TODO: we need to decide whether or not we go to the user-info app to check if the user exists
		CascaioUser user = entityManager.find(CascaioUser.class, userId);

		if (null == user) {
			user = new CascaioUser(userId);
			entityManager.persist(user);
		}

		this.user = user;
	}
}

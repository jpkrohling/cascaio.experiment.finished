package com.cascaio.backend.v1.entity;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 10:32 AM
 */
public class BasicPersistenceUnitTest {
	private EntityManager entityManager;

	@Before
	public void setup() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("backend-unittest-pu");
		this.entityManager = entityManagerFactory.createEntityManager();
	}

	@Test
	public void testIsAbleToPersistFinancialInstitution() {
		BaseEntityTest baseEntityTest = new BaseEntityTest();

		entityManager.getTransaction().begin();
		entityManager.persist(baseEntityTest);
		entityManager.getTransaction().commit();
	}

	@Entity
	public class BaseEntityTest extends NamedCascaioEntity {

	}
}

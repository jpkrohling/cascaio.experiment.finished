package com.cascaio.backend.v1.boundary.referencedata;

import com.cascaio.backend.v1.entity.MutualFund;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * User: jpkrohling
 * Date: 9/25/13
 * Time: 8:54 PM
 */
public class MutualFundServiceTest {
	private EntityManager entityManager;
	private MutualFundService service = new MutualFundService();

	@Before
	public void setup() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("backend-unittest-pu");
		entityManager = entityManagerFactory.createEntityManager();

		service.entityManager = entityManager;
		service.logger = LoggerFactory.getLogger(MutualFundQuoteService.class);
	}

	@Test
	public void testCreate() {
		entityManager.getTransaction().begin();
		assertNull("There should be no Bayern AG", service.getByIsin("DE000BAY0017"));
		service.create("DE000BAY0017", "BAY001", "Bayern AG", "EUR");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		assertNotNull("There should be Bayern AG", service.getByIsin("DE000BAY0017"));
		entityManager.getTransaction().commit();
	}

	@Test
	public void testGet() {
		entityManager.getTransaction().begin();
		MutualFund fund = service.create("DE000BAY0017", "BAY001", "Bayern AG", "EUR");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		fund = service.get(fund.getId());
		assertEquals("Fund name should be Bayern AG", "Bayern AG", fund.getName());
		assertEquals("The ISIN should be DE000BAY0017", "DE000BAY0017", fund.getIsin());
		assertEquals("The currency should be EUR", CurrencyUnit.EUR, fund.getCurrency());
		assertEquals("WKN should be BAY001", "BAY001", fund.getWkn());
		entityManager.getTransaction().commit();
	}

	@Test
	public void testUpdate() {
		entityManager.getTransaction().begin();
		MutualFund fund = service.create("DE000BAY0017", "BAY001", "Bayern AG", "EUR");
		service.update(fund.getId(), "BAY002", "Bayern AS", "GBP");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		fund = service.get(fund.getId());
		assertEquals("Fund name should be changed", "Bayern AS", fund.getName());
		assertEquals("Currency should be changed", CurrencyUnit.GBP, fund.getCurrency());
		assertEquals("Currency should be changed", "BAY002", fund.getWkn());
		entityManager.getTransaction().commit();
	}

	@Test(expected = NoResultException.class)
	public void testDelete() {
		entityManager.getTransaction().begin();
		MutualFund fund = service.create("DE000BAY0017", "BAY001", "Bayern AG", "EUR");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		service.delete(fund.getId());
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		service.get(fund.getId());
		entityManager.getTransaction().commit();
		entityManager.clear();
	}

	@Test
	public void testList() {
		entityManager.getTransaction().begin();
		service.create("DE000BAY0017", "BAY001", "Bayern AG", "EUR");
		service.create("DE000BAY0018", "BAY002", "Bayern 2 AG", "USD");
		service.create("DE000BAY0019", "BAY003", "Bayern 3 AG", "GBP");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		List<MutualFund> funds = service.list();
		assertEquals("There should be 3 funds", 3, funds.size());
		entityManager.getTransaction().commit();
		entityManager.clear();
	}

}

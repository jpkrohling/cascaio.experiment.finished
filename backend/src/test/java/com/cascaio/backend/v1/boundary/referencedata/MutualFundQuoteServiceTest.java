package com.cascaio.backend.v1.boundary.referencedata;

import com.cascaio.backend.v1.entity.MutualFundQuote;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * User: jpkrohling
 * Date: 9/12/13
 * Time: 8:34 PM
 */
public class MutualFundQuoteServiceTest {
	private EntityManager entityManager;
	private MutualFundQuoteService service = new MutualFundQuoteService();
	private MutualFundService mutualFundService = new MutualFundService();

	@Before
	public void setup() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("backend-unittest-pu");
		entityManager = entityManagerFactory.createEntityManager();

		service.mutualFundService = mutualFundService;
		service.entityManager = entityManager;
		mutualFundService.entityManager = entityManager;
		service.logger = LoggerFactory.getLogger(MutualFundQuoteService.class);
		mutualFundService.logger = LoggerFactory.getLogger(MutualFundService.class);
	}

	@Test
	public void testCreateWithoutExistingMutualFund() {
		entityManager.getTransaction().begin();
		assertNull("There should be no Bayern AG", mutualFundService.getByIsin("DE000BAY0017"));
		service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.45", "2013-09-12", "EUR");
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		assertNotNull("There should be Bayern AG", mutualFundService.getByIsin("DE000BAY0017"));
		entityManager.getTransaction().commit();

	}

	@Test
	public void testCreateWithExistingMutualFund() {
		entityManager.getTransaction().begin();
		assertNull("There should be no Bayern AG", mutualFundService.getByIsin("DE000BAY0017"));
		mutualFundService.create("DE000BAY0017", "BAY001", "Bayern AG", "EUR");
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		assertNotNull("There should be Bayern AG", mutualFundService.getByIsin("DE000BAY0017"));
		service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.45", "2013-09-12", "EUR");
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		assertNotNull("There should still be Bayern AG", mutualFundService.getByIsin("DE000BAY0017"));
		entityManager.getTransaction().commit();

	}

	@Test
	public void testCreateWithExistingQuote() {
		entityManager.getTransaction().begin();
		MutualFundQuote quote = service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.45", "2013-09-12", "EUR");
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		assertNotNull("The quote should exist", service.get(quote.getId()));
		quote = service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.45", "2013-09-12", "EUR");
		assertNull("There should not be a new quote", quote);
		entityManager.getTransaction().commit();
	}

	@Test
	public void testListByISIN() {
		entityManager.getTransaction().begin();
		service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.45", "2013-09-10", "EUR");
		service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.46", "2013-09-11", "EUR");
		service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.47", "2013-09-12", "EUR");
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		List<MutualFundQuote> mutualFundQuotes = service.list(null, "DE000BAY0017");
		entityManager.getTransaction().commit();

		assertEquals("There should be three quotes", 3, mutualFundQuotes.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testListByAllNull() {
		entityManager.getTransaction().begin();
		service.list(null, null);
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListByAllEmpty() {
		entityManager.getTransaction().begin();
		service.list("", "");
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListByEmptyISIN() {
		entityManager.getTransaction().begin();
		service.list(null, "");
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListByEmptyId() {
		entityManager.getTransaction().begin();
		service.list("", null);
		entityManager.getTransaction().commit();
	}

	@Test
	public void testListByNonExistingId() {
		entityManager.getTransaction().begin();
		List<MutualFundQuote> mutualFundQuotes = service.list("invalid-id", null);
		entityManager.getTransaction().commit();
		assertNull("There should be no quotes", mutualFundQuotes);
	}

	@Test
	public void testListByNonExistingISIN() {
		entityManager.getTransaction().begin();
		List<MutualFundQuote> mutualFundQuotes = service.list(null, "invalid-isin");
		entityManager.getTransaction().commit();
		assertNull("There should be no quotes", mutualFundQuotes);
	}

	@Test
	public void testGetSpecificQuote() {
		entityManager.getTransaction().begin();
		MutualFundQuote createdQuote = service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.45", "2013-09-12", "EUR");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		MutualFundQuote retrievedQuote = service.get(createdQuote.getId());
		entityManager.getTransaction().commit();

		assertEquals("Quotes should be the same", createdQuote, retrievedQuote);
	}

	@Test
	public void testDeleteSpecificQuote() {
		entityManager.getTransaction().begin();
		MutualFundQuote quote = service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.45", "2013-09-12", "EUR");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		service.delete(quote.getId());
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		assertEquals("Quote should not exist", 0, service.list(quote.getMutualFund().getId(), null).size());
		entityManager.getTransaction().commit();
	}

	@Test(expected = NoResultException.class)
	public void testDeleteNonExistingQuote() {
		entityManager.getTransaction().begin();
		service.delete("invalid-id");
		entityManager.getTransaction().commit();
	}

	@Test
	public void testUpdateExistingQuote() {
		entityManager.getTransaction().begin();
		MutualFundQuote quote = service.create("DE000BAY0017", "BAY001", "Bayern AG", "123.45", "2013-09-12", "EUR");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		service.update(quote.getId(), "123.46");
		entityManager.getTransaction().commit();
	}

	@Test(expected = NoResultException.class)
	public void testUpdateNonExistingQuote() {
		entityManager.getTransaction().begin();
		service.update("invalid-id", "123.46");
		entityManager.getTransaction().commit();
	}


}

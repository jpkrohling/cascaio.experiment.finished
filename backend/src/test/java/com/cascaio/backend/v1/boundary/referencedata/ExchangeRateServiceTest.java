package com.cascaio.backend.v1.boundary.referencedata;

import com.cascaio.backend.v1.entity.ExchangeRate;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * User: jpkrohling
 * Date: 7/9/13
 * Time: 6:54 PM
 */
public class ExchangeRateServiceTest {
	private ExchangeRateService service = new ExchangeRateService();
	private EntityManager entityManager;

	@Before
	public void setup() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("backend-unittest-pu");
		entityManager = entityManagerFactory.createEntityManager();

		service.entityManager = entityManager;
		service.logger = LoggerFactory.getLogger(ExchangeRateService.class);
	}

	@Test
	public void testCreateValidRate() {
		entityManager.getTransaction().begin();
		ExchangeRate rate = service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		assertEquals(CurrencyUnit.EUR, rate.getCurrencyFrom());
		assertEquals(CurrencyUnit.USD, rate.getCurrencyTo());
		assertEquals(new BigDecimal("1.2786"), rate.getPrice());
		assertEquals(new DateMidnight(2013, 7, 8, DateTimeZone.UTC).toDateTime(), rate.getDate());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateWithInvalidRate() {
		entityManager.getTransaction().begin();
		service.create("EUR", "USD", "invalid-rate", "2013-07-08");
		entityManager.getTransaction().commit();
	}

	@Test
	public void testCreateDuplicateRate() {
		entityManager.getTransaction().begin();
		service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		ExchangeRate rate = service.create("EUR", "USD", "1.2786", "2013-07-08");
		assertNull("Duplicate rate should not have been created", rate);
		entityManager.getTransaction().commit();
		entityManager.clear();
	}

	@Test
	public void testUpdateRate() {
		entityManager.getTransaction().begin();
		ExchangeRate rate = service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		ExchangeRate updatedRate = service.update(rate.getId(), "1.39");
		entityManager.getTransaction().commit();
		entityManager.clear();

		assertNotNull("Updated rate should not be null", updatedRate);
		assertEquals("Rate should be updated", new BigDecimal("1.39"), updatedRate.getPrice());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateWithNullRate() {
		entityManager.getTransaction().begin();
		ExchangeRate rate = service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		service.update(rate.getId(), null);
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateWithEmptyRate() {
		entityManager.getTransaction().begin();
		ExchangeRate rate = service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		service.update(rate.getId(), "");
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateWithInvalidRate() {
		entityManager.getTransaction().begin();
		ExchangeRate rate = service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		service.update(rate.getId(), "invalid-number");
		entityManager.getTransaction().commit();
	}

	@Test
	public void testListExchangeRates() {
		entityManager.getTransaction().begin();
		service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		List<ExchangeRate> rates = service.list("EUR", "USD");
		assertEquals("There should be one rate", 1, rates.size());
		entityManager.getTransaction().commit();
	}

	@Test
	public void testListEmptyResults() {
		entityManager.getTransaction().begin();
		service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		List<ExchangeRate> rates = service.list("EUR", "AUD");
		assertEquals("There should be no rates", 0, rates.size());
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListExchangeRatesWithNullCurrencies() {
		entityManager.getTransaction().begin();
		service.list(null, null);
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListExchangeRatesWithEmptyCurrencies() {
		entityManager.getTransaction().begin();
		service.list("", "");
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListExchangeRatesWithNullBaseCurrency() {
		entityManager.getTransaction().begin();
		service.list(null, "EUR");
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListExchangeRatesWithNullTargetCurrency() {
		entityManager.getTransaction().begin();
		service.list("USD", null);
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListExchangeRatesWithEmptyBaseCurrency() {
		entityManager.getTransaction().begin();
		service.list("", "EUR");
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListExchangeRatesWithEmptyTargetCurrency() {
		entityManager.getTransaction().begin();
		service.list("USD", "");
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListExchangeRatesWithInvalidBaseCurrency() {
		entityManager.getTransaction().begin();
		service.list("BTC", "EUR");
		entityManager.getTransaction().commit();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListExchangeRatesWithInvalidTargetCurrency() {
		entityManager.getTransaction().begin();
		service.list("EUR", "BTC");
		entityManager.getTransaction().commit();
	}

	@Test
	public void testRetrieveSpecificRate() {
		entityManager.getTransaction().begin();
		ExchangeRate createdRate = service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		ExchangeRate retrievedRate = service.get(createdRate.getId());
		entityManager.getTransaction().commit();

		assertEquals(createdRate, retrievedRate);
	}

	@Test(expected = NoResultException.class)
	public void testRetrieveNonExistingRate() {
		entityManager.getTransaction().begin();
		service.get("invalid-rate");
		entityManager.getTransaction().commit();
	}

	@Test
	public void testDeleteExistingRate() {
		entityManager.getTransaction().begin();
		ExchangeRate createdRate = service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		entityManager.getTransaction().begin();
		assertEquals("There should be one rate", 1, service.list("EUR", "USD").size());
		service.delete(createdRate.getId());
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		assertEquals("There should be no rates", 0, service.list("EUR", "USD").size());
		entityManager.getTransaction().commit();
	}

}

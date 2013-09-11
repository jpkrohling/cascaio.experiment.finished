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
import javax.persistence.Persistence;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

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
		service.logger = LoggerFactory.getLogger(FinancialInstitutionService.class);
	}

	@Test
	public void create() {
		entityManager.getTransaction().begin();
		ExchangeRate rate = service.create("EUR", "USD", "1.2786", "2013-07-08");
		entityManager.getTransaction().commit();
		entityManager.clear();

		assertEquals(CurrencyUnit.EUR, rate.getCurrencyFrom());
		assertEquals(CurrencyUnit.USD, rate.getCurrencyTo());
		assertEquals(new BigDecimal("1.2786"), rate.getPrice());
		assertEquals(new DateMidnight(2013, 7, 8, DateTimeZone.UTC).toDateTime(), rate.getDate());
	}

}

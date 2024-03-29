package com.cascaio.backend.v1.boundary.referencedata;

import com.cascaio.backend.v1.control.RequiresReferenceDataApplication;
import com.cascaio.backend.v1.control.validation.Currency;
import com.cascaio.backend.v1.control.validation.ISODate;
import com.cascaio.backend.v1.control.validation.NumericRate;
import com.cascaio.backend.v1.entity.ExchangeRate;
import com.cascaio.backend.v1.entity.ExchangeRate_;
import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

/**
 * User: jpkrohling
 * Date: 7/7/13
 * Time: 4:54 PM
 */
@Stateless
@Path("/exchangeRates")
@RequiresReferenceDataApplication
public class ExchangeRateService {
	private DateTimeFormatter inputDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();

	@Inject
	EntityManager entityManager;

	@Inject
	Logger logger;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ExchangeRate> list(@NotNull @Currency @FormParam("currencyFrom") String sCurrencyFrom,
								   @NotNull @Currency @FormParam("currencyTo") String sCurrencyTo) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ExchangeRate> query = builder.createQuery(ExchangeRate.class);

		if ((null == sCurrencyFrom || sCurrencyFrom.isEmpty()) || (null == sCurrencyTo || sCurrencyTo.isEmpty())) {
			throw new IllegalArgumentException("A pair of currencies is required to query the exchange rates.");
		}

		Root<ExchangeRate> root = query.from(ExchangeRate.class);
		query.select(root);

		CurrencyUnit currencyTo = CurrencyUnit.of(sCurrencyTo);
		CurrencyUnit currencyFrom = CurrencyUnit.of(sCurrencyFrom);

		query.where(
				builder.equal(root.get(ExchangeRate_.currencyTo), currencyTo),
				builder.equal(root.get(ExchangeRate_.currencyFrom), currencyFrom));

		logger.trace("Listing all exchange rates we have");
		return entityManager.createQuery(query).getResultList();
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ExchangeRate get(@PathParam("id") String id) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ExchangeRate> query = builder.createQuery(ExchangeRate.class);
		Root<ExchangeRate> root = query.from(ExchangeRate.class);
		query.select(root);
		query.where(builder.equal(root.get(ExchangeRate_.id), id));

		logger.trace("Got a request for exchange rate ID: {}", id);
		return entityManager.createQuery(query).getSingleResult();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public ExchangeRate create(
			@NotNull @Currency @FormParam("currencyFrom") String sCurrencyFrom,
			@NotNull @Currency @FormParam("currencyTo") String sCurrencyTo,
			@NotNull @FormParam("rate") String sRate,
			@NotNull @ISODate @FormParam("date") String sDate) {

		if (logger.isTraceEnabled()) {
			Object[] values = new Object[]{sCurrencyFrom, sCurrencyTo, sRate, sDate};
			logger.trace("Asked to create an exchange rate from {} to {} on the amount {} for date {}", values);
		}

		CurrencyUnit currencyFrom = CurrencyUnit.of(sCurrencyFrom);
		CurrencyUnit currencyTo = CurrencyUnit.of(sCurrencyTo);
		BigDecimal rate = new BigDecimal(sRate);
		DateTime date = DateMidnight.parse(sDate, inputDateFormat).toDateTime();

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ExchangeRate> query = builder.createQuery(ExchangeRate.class);
		Root<ExchangeRate> root = query.from(ExchangeRate.class);

		query.select(root);
		query.where(
				builder.equal(root.get(ExchangeRate_.currencyFrom), currencyFrom),
				builder.equal(root.get(ExchangeRate_.currencyTo), currencyTo),
				builder.equal(root.get(ExchangeRate_.date), date)
		);

		if (entityManager.createQuery(query).getResultList().size() > 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("We've seen a quote for {}-{} on {} already. Ignoring request.", new Object[]{currencyFrom, currencyTo, date});
			}
			return null;
		}

		ExchangeRate exchangeRate = new ExchangeRate(currencyFrom, currencyTo, rate, date);
		entityManager.persist(exchangeRate);

		return exchangeRate;
	}

	@Path("{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public ExchangeRate update(@PathParam("id") String id,
							   @NotNull @NumericRate @FormParam("rate") String sRate) {

		if (null == sRate || sRate.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'rate' is required for updating an exchange rate");
		}

		ExchangeRate exchangeRate = get(id);
		exchangeRate.setPrice(new BigDecimal(sRate));

		entityManager.persist(exchangeRate);
		logger.trace("Updated the exchange rate with ID {}", id);
		return exchangeRate;
	}

	@Path("{id}")
	@DELETE
	public void delete(@PathParam("id") String id) {
		ExchangeRate exchangeRate = get(id);
		logger.trace("Deleting the exchange rate with ID {}", id);
		entityManager.remove(exchangeRate);
	}
}

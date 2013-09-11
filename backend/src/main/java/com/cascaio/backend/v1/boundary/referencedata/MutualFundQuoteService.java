package com.cascaio.backend.v1.boundary.referencedata;

import com.cascaio.backend.v1.control.RequiresReferenceDataApplication;
import com.cascaio.backend.v1.control.validation.Currency;
import com.cascaio.backend.v1.control.validation.ISODate;
import com.cascaio.backend.v1.control.validation.NumericRate;
import com.cascaio.backend.v1.entity.MutualFund;
import com.cascaio.backend.v1.entity.MutualFundQuote;
import com.cascaio.backend.v1.entity.MutualFundQuote_;
import com.cascaio.backend.v1.entity.MutualFund_;
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
 * Date: 9/10/13
 * Time: 8:25 PM
 */
@Stateless
@Path("/mutualFundQuotes")
@RequiresReferenceDataApplication
public class MutualFundQuoteService {
	private DateTimeFormatter inputDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();

	@Inject
	EntityManager entityManager;

	@Inject
	Logger logger;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<MutualFundQuote> list() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MutualFundQuote> query = builder.createQuery(MutualFundQuote.class);

		Root<MutualFundQuote> root = query.from(MutualFundQuote.class);
		query.select(root);
		logger.trace("Listing all mutual fund quotes we have");
		return entityManager.createQuery(query).getResultList();
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public MutualFundQuote get(@PathParam("id") String id) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MutualFundQuote> query = builder.createQuery(MutualFundQuote.class);
		Root<MutualFundQuote> root = query.from(MutualFundQuote.class);
		query.select(root);
		query.where(builder.equal(root.get(MutualFundQuote_.id), id));

		logger.trace("Got a request for mutual fund quote ID: {}", id);
		return entityManager.createQuery(query).getSingleResult();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public MutualFundQuote create(
			@NotNull @FormParam("isin") String isin,
			@NotNull @FormParam("wkn") String wkn,
			@NotNull @FormParam("name") String name,
			@NotNull @FormParam("quote") String sQuote,
			@NotNull @ISODate @FormParam("date") String sDate,
			@NotNull @Currency @FormParam("currency") String sCurrency) {

		if (logger.isTraceEnabled()) {
			Object[] values = new Object[]{isin, sQuote, sDate};
			logger.trace("Asked to create an mutual fund quote for fund with ISIN {} on the amount {} for date {}", values);
		}
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CurrencyUnit currency = CurrencyUnit.of(sCurrency);
		BigDecimal quote = new BigDecimal(sQuote);
		DateTime date = DateMidnight.parse(sDate, inputDateFormat).toDateTime();

		CriteriaQuery<MutualFund> queryFund = builder.createQuery(MutualFund.class);
		Root<MutualFund> rootFund = queryFund.from(MutualFund.class);
		queryFund.select(rootFund);
		queryFund.where(
				builder.equal(rootFund.get(MutualFund_.isin), isin)
		);

		List<MutualFund> funds = entityManager.createQuery(queryFund).getResultList();

		if (funds.size() > 1) {
			// something really wrong... ISIN is unique! No option, but to throw an exception
			throw new IllegalStateException("Two or more funds share the same ISIN. This is a big problem. Contact the system administrator.");
		}

		MutualFund fund = null;
		if (funds.size() == 0) {
			fund = new MutualFund(name, isin, currency);
			fund.setWkn(wkn);
			entityManager.persist(fund);
		} else {
			fund = funds.get(0);
		}

		CriteriaQuery<MutualFundQuote> query = builder.createQuery(MutualFundQuote.class);
		Root<MutualFundQuote> root = query.from(MutualFundQuote.class);

		query.select(root);
		query.where(
				builder.equal(root.get(MutualFundQuote_.mutualFund), fund),
				builder.equal(root.get(MutualFundQuote_.date), date)
		);

		if (entityManager.createQuery(query).getResultList().size() > 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("We've seen a quote for {} on {} already. Ignoring request.", new Object[]{isin, date});
			}
			return null;
		}

		MutualFundQuote mutualFundQuote = fund.addQuote(date, quote);
		entityManager.persist(fund);

		return mutualFundQuote;
	}

	@Path("{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public MutualFundQuote update(@PathParam("id") String id,
							   @NumericRate @FormParam("rate") String sRate,
							   @ISODate @FormParam("date") String sDate) {
		MutualFundQuote mutualFundQuote = get(id);

		if (null != sRate && !sRate.isEmpty()) mutualFundQuote.setPrice(new BigDecimal(sRate));
		if (null != sDate && !sDate.isEmpty()) mutualFundQuote.setDate(DateMidnight.parse(sDate, inputDateFormat).toDateTime());

		entityManager.persist(mutualFundQuote);
		logger.trace("Updated the mutual fund quote with ID {}", id);
		return mutualFundQuote;
	}

	@Path("{id}")
	@DELETE
	public void delete(@PathParam("id") String id) {
		MutualFundQuote mutualFundQuote = get(id);
		logger.trace("Deleting the mutual fund quote with ID {}", id);
		entityManager.remove(mutualFundQuote);
	}
}

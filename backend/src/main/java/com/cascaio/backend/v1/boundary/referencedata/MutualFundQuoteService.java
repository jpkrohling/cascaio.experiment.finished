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
import javax.persistence.NoResultException;
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

	@Inject
	MutualFundService mutualFundService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<MutualFundQuote> list(@QueryParam("fundId") String fundId,
									  @QueryParam("isin") String isin) {

		if ((null == isin || isin.isEmpty()) && (null == fundId || fundId.isEmpty())) {
			throw new IllegalArgumentException("An ISIN or Mutual Fund ID is required when listing fund quotes");
		}

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MutualFundQuote> query = builder.createQuery(MutualFundQuote.class);

		Root<MutualFundQuote> root = query.from(MutualFundQuote.class);
		query.select(root);

		MutualFund fund = null;
		if (null != fundId && !fundId.isEmpty()) {
			try {
				fund = mutualFundService.get(fundId);
			} catch (NoResultException nre) {
				logger.error("Caller requested fund with ID {}", fundId);
				return null;
			}
		}

		if (null != isin && !isin.isEmpty()) {
			fund = mutualFundService.getByIsin(isin);
		}

		if (null == fund) {
			return null;
		}

		logger.trace("Listing all mutual fund quotes we have");
		return fund.getQuotes();
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public MutualFundQuote get(@NotNull @PathParam("id") String id) {
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

		BigDecimal quote = new BigDecimal(sQuote);
		DateTime date = DateMidnight.parse(sDate, inputDateFormat).toDateTime();

		MutualFund fund = mutualFundService.getByIsin(isin);
		if (null == fund) {
			fund = mutualFundService.create(isin, wkn, name, sCurrency);
		}

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MutualFundQuote> query = builder.createQuery(MutualFundQuote.class);
		Root<MutualFundQuote> root = query.from(MutualFundQuote.class);

		query.select(root);
		query.where(
				builder.equal(root.get(MutualFundQuote_.mutualFund), fund),
				builder.equal(root.get(MutualFundQuote_.date), date)
		);

		if (entityManager.createQuery(query).getResultList().size() > 0) {
			if (logger.isWarnEnabled()) {
				logger.info("We've seen a quote for {} on {} already. Ignoring request.", new Object[]{isin, date});
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
							   @NumericRate @FormParam("rate") String sRate) {
		MutualFundQuote mutualFundQuote = get(id);

		if (null != sRate && !sRate.isEmpty()) mutualFundQuote.setPrice(new BigDecimal(sRate));

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

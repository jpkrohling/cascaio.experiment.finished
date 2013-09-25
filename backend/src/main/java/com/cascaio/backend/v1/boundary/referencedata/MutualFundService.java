package com.cascaio.backend.v1.boundary.referencedata;

import com.cascaio.backend.v1.control.RequiresReferenceDataApplication;
import com.cascaio.backend.v1.control.validation.Currency;
import com.cascaio.backend.v1.entity.MutualFund;
import com.cascaio.backend.v1.entity.MutualFund_;
import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.joda.money.CurrencyUnit;
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
import java.util.List;

/**
 * User: jpkrohling
 * Date: 9/12/13
 * Time: 8:56 PM
 */
@Stateless
@Path("/mutualFundQuotes")
@RequiresReferenceDataApplication
public class MutualFundService {
	@Inject
	EntityManager entityManager;

	@Inject
	Logger logger;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<MutualFund> list() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MutualFund> query = builder.createQuery(MutualFund.class);
		Root<MutualFund> root = query.from(MutualFund.class);
		query.select(root);
		return entityManager.createQuery(query).getResultList();
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public MutualFund get(@PathParam("id") String id) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MutualFund> query = builder.createQuery(MutualFund.class);
		Root<MutualFund> root = query.from(MutualFund.class);
		query.select(root);
		query.where(builder.equal(root.get(MutualFund_.id), id));

		logger.trace("Retrieving mutual fund with ID: {}", id);
		return entityManager.createQuery(query).getSingleResult();
	}

	@Path("isin/{isin}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public MutualFund getByIsin(@PathParam("isin") String isin) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MutualFund> query = builder.createQuery(MutualFund.class);
		Root<MutualFund> root = query.from(MutualFund.class);
		query.select(root);
		query.where(builder.equal(root.get(MutualFund_.isin), isin));

		logger.trace("Retrieving mutual fund with ISIN: {}", isin);

		List<MutualFund> mutualFunds = entityManager.createQuery(query).getResultList();

		if (mutualFunds.size() > 1) {
			logger.error("More than one fund found for ISIN {}. Contact the administrator.", isin);
			throw new IllegalStateException("More than one fund found for ISIN "+isin+". Contact the administrator.");
		}

		if (mutualFunds.size() > 0) {
			return mutualFunds.get(0);
		}

		return null;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public MutualFund create(
			@NotNull @FormParam("isin") String isin,
			@NotNull @FormParam("wkn") String wkn,
			@NotNull @FormParam("name") String name,
			@NotNull @Currency @FormParam("currency") String sCurrency) {

		CurrencyUnit currency = CurrencyUnit.of(sCurrency);
		MutualFund fund = getByIsin(isin);

		if (null == fund) {
			fund = new MutualFund(name, isin, currency);
			fund.setWkn(wkn);
			entityManager.persist(fund);
		}

		return fund;
	}

	@Path("{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public MutualFund update(@PathParam("id") String id,
							 @FormParam("wkn") String wkn,
							 @FormParam("name") String name,
							 @Currency @FormParam("currency") String sCurrency) {
		MutualFund fund = get(id);

		CurrencyUnit currency = null;
		if (null != sCurrency) {
			currency = CurrencyUnit.of(sCurrency);
		}

		if(null != wkn) fund.setWkn(wkn);
		if(null != sCurrency) fund.setCurrency(currency);
		if(null != name) fund.setName(name);

		entityManager.persist(fund);
		logger.trace("Updated the mutual fund with ID {}", id);
		return fund;
	}

	@Path("{id}")
	@DELETE
	public void delete(@PathParam("id") String id) {
		MutualFund fund = get(id);
		logger.trace("Deleting the mutual fund with ID {}", id);
		entityManager.remove(fund);
	}
}

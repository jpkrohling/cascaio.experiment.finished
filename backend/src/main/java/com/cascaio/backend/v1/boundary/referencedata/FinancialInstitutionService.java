package com.cascaio.backend.v1.boundary.referencedata;

import com.cascaio.backend.v1.control.RequiresReferenceDataApplication;
import com.cascaio.backend.v1.control.validation.BIC;
import com.cascaio.backend.v1.control.validation.Bankleitzahl;
import com.cascaio.backend.v1.control.validation.CNPJ;
import com.cascaio.backend.v1.control.validation.Country;
import com.cascaio.backend.v1.entity.FinancialInstitution;
import com.cascaio.backend.v1.entity.FinancialInstitution_;
import org.jboss.resteasy.spi.validation.ValidateRequest;
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
 * Date: 6/22/13
 * Time: 10:13 AM
 */
@Stateless
@Path("/financialInstitutions")
@RequiresReferenceDataApplication
public class FinancialInstitutionService {

	@Inject
	EntityManager entityManager;

	@Inject
	Logger logger;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<FinancialInstitution> list() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FinancialInstitution> query = builder.createQuery(FinancialInstitution.class);

		Root<FinancialInstitution> root = query.from(FinancialInstitution.class);
		query.select(root);

		return entityManager.createQuery(query).getResultList();
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public FinancialInstitution get(@PathParam("id") String id) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FinancialInstitution> query = builder.createQuery(FinancialInstitution.class);
		Root<FinancialInstitution> root = query.from(FinancialInstitution.class);
		query.select(root);
		query.where(builder.equal(root.get(FinancialInstitution_.id), id));

		return entityManager.createQuery(query).getSingleResult();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public FinancialInstitution create(
			@NotNull @FormParam("name") String name,
			@NotNull @Country @FormParam("country") String country,
			@CNPJ @FormParam("cnpj") String cnpj,
			@Bankleitzahl @FormParam("bankleitzahl") String blz,
			@BIC @FormParam("bic") String bic,
			@FormParam("codCompensacao") String codCompensacao) {
		FinancialInstitution financialInstitution = new FinancialInstitution(name, country);
		financialInstitution.setBankleitzahl(blz);
		financialInstitution.setBic(bic);
		financialInstitution.setCnpj(cnpj);
		financialInstitution.setCodCompensacao(codCompensacao);
		entityManager.persist(financialInstitution);
		return financialInstitution;
	}

	@Path("{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateRequest
	public FinancialInstitution update(@PathParam("id") String id,
									   @FormParam("name") String name,
									   @CNPJ @FormParam("cnpj") String cnpj,
									   @Bankleitzahl @FormParam("bankleitzahl") String blz,
									   @BIC @FormParam("bic") String bic,
									   @FormParam("codCompensacao") String codCompensacao) {
		FinancialInstitution financialInstitution = get(id);

		if (null != name && !name.isEmpty()) financialInstitution.setName(name);
		if (null != cnpj && !cnpj.isEmpty()) financialInstitution.setCnpj(cnpj);
		if (null != blz && !blz.isEmpty()) financialInstitution.setBankleitzahl(blz);
		if (null != bic && !bic.isEmpty()) financialInstitution.setBic(bic);
		if (null != codCompensacao && !codCompensacao.isEmpty()) financialInstitution.setCodCompensacao(codCompensacao);
		entityManager.persist(financialInstitution);
		return financialInstitution;
	}

	@Path("{id}")
	@DELETE
	public void delete(@PathParam("id") String id) {
		FinancialInstitution financialInstitution = get(id);
		entityManager.remove(financialInstitution);
	}

}

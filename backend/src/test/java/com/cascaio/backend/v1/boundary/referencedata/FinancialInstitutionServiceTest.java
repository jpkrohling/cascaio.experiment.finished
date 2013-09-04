package com.cascaio.backend.v1.boundary.referencedata;

import com.cascaio.backend.v1.entity.FinancialInstitution;
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

/**
 * User: jpkrohling
 * Date: 7/7/13
 * Time: 3:06 PM
 */
public class FinancialInstitutionServiceTest {
	private FinancialInstitutionService service = new FinancialInstitutionService();
	private EntityManager entityManager;

	@Before
	public void setup() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("backend-unittest-pu");
		entityManager = entityManagerFactory.createEntityManager();

		service.entityManager = entityManager;
		service.logger = LoggerFactory.getLogger(FinancialInstitutionService.class);
	}

	@Test
	public void createGermanInstitution() {
		entityManager.getTransaction().begin();
		FinancialInstitution fromService = service.create(
				"Sparda-Bank München",
				"DE",
				null,
				"70090500",
				"GENODEF1S04",
				null
		);
		entityManager.getTransaction().commit();
		entityManager.clear();

		FinancialInstitution fromDatabase = entityManager.find(FinancialInstitution.class, fromService.getId());
		assertFromServiceIsEqualsToFromDatabase(fromService, fromDatabase);
	}

	@Test
	public void createBrazilianInstitution() {
		entityManager.getTransaction().begin();
		FinancialInstitution fromService = service.create(
				"Banco do Brasil SA",
				"BR",
				"00000000000000",
				null,
				null,
				"001"
		);
		entityManager.getTransaction().commit();
		entityManager.clear();

		FinancialInstitution fromDatabase = entityManager.find(FinancialInstitution.class, fromService.getId());
		assertFromServiceIsEqualsToFromDatabase(fromService, fromDatabase);
	}

	@Test
	public void updateWithAlmostAllParametersAsNull() {
		FinancialInstitution fromService = getCreatedFinancialInstitution();

		entityManager.getTransaction().begin();
		fromService = service.update(fromService.getId(), null, null, null, "", "changed-codCompensacao");
		entityManager.getTransaction().commit();
		entityManager.clear();

		FinancialInstitution fromDatabase = entityManager.find(FinancialInstitution.class, fromService.getId());

		assertFromServiceIsEqualsToFromDatabase(fromService, fromDatabase);
		assertEquals("changed-codCompensacao", fromService.getCodCompensacao());
	}

	@Test
	public void updateWithAlmostAllParametersEmpty() {
		FinancialInstitution fromService = getCreatedFinancialInstitution();

		entityManager.getTransaction().begin();
		fromService = service.update(fromService.getId(), "", "", "", "", "changed-codCompensacao-2");
		entityManager.getTransaction().commit();
		entityManager.clear();

		FinancialInstitution fromDatabase = entityManager.find(FinancialInstitution.class, fromService.getId());

		assertFromServiceIsEqualsToFromDatabase(fromService, fromDatabase);
		assertEquals("changed-codCompensacao-2", fromService.getCodCompensacao());
	}

	@Test
	public void remove() {
		FinancialInstitution fromService = getCreatedFinancialInstitution();
		String id = fromService.getId();

		entityManager.getTransaction().begin();
		service.delete(id);
		entityManager.getTransaction().commit();
		entityManager.clear();

		FinancialInstitution empty = entityManager.find(FinancialInstitution.class, id);

		assertEquals(null, empty);

	}

	@Test
	public void list() {
		for (int i = 0 ; i < 10 ; i++) {
			getCreatedFinancialInstitution();
		}
		List<FinancialInstitution> list = service.list();
		assertEquals(10, list.size());
	}

	@Test
	public void get() {
		FinancialInstitution fromService = getCreatedFinancialInstitution();
		String id = fromService.getId();
		fromService = service.get(fromService.getId());
		entityManager.clear();

		FinancialInstitution fromDatabase = entityManager.find(FinancialInstitution.class, id);
		assertFromServiceIsEqualsToFromDatabase(fromService, fromDatabase);
		assertNotNull(fromService.getName());
	}

	@Test(expected = NoResultException.class)
	public void getNonExistingRecord() {
		service.get("non-existent-id");
	}

	private FinancialInstitution getCreatedFinancialInstitution() {
		entityManager.getTransaction().begin();
		FinancialInstitution created = service.create(
				"Sparda-Bank München",
				"DE",
				null,
				"70090500",
				"GENODEF1S04",
				null
		);
		entityManager.getTransaction().commit();
		entityManager.clear();
		return created;
	}

	private void assertFromServiceIsEqualsToFromDatabase(FinancialInstitution fromService, FinancialInstitution fromDatabase) {
		assertEquals(fromService.getName(), fromDatabase.getName());
		assertEquals(fromService.getCnpj(), fromDatabase.getCnpj());
		assertEquals(fromService.getBankleitzahl(), fromDatabase.getBankleitzahl());
		assertEquals(fromService.getBic(), fromDatabase.getBic());
		assertEquals(fromService.getCountry(), fromDatabase.getCountry());
		assertEquals(fromService.getCodCompensacao(), fromDatabase.getCodCompensacao());
	}

}

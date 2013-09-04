package com.cascaio.appinfo.v1.entity;

import com.cascaio.security.KeyGenerator;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * User: jpkrohling
 * Date: 2013-05-11 8:14 PM
 */
public class ApplicationUnitTest {
	private EntityManager entityManager;

	@Before
	public void setup() {
		System.setProperty("cascaio.appinfo.jasypt.password", "private-key-for-unit-test");
		System.setProperty("cascaio.appinfo.properties.filepath", "classpath:/cascaio-appinfo.sample.properties");

		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("appinfo-unittest-pu");
		this.entityManager = entityManagerFactory.createEntityManager();
	}

	@Test
	public void testNameAndAccessKeyAreEncrypted() {
		entityManager.getTransaction().begin();
		final String accessKey = KeyGenerator.generate();
		final String secretKey = KeyGenerator.generate();
		final Application application = new Application("testNameTransformation", accessKey, secretKey, ApplicationType.REFERENCE_DATA);
		entityManager.persist(application);
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		entityManager.unwrap(Session.class).doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				PreparedStatement pstmt = connection.prepareStatement("select name, accessKey from application where id = ?");
				pstmt.setString(1, application.getId());
				ResultSet rs = pstmt.executeQuery();

				rs.next();
				String nameFromDb = rs.getString("name");
				String accessKeyFromDb = rs.getString("name");
				String msgName = "Name in the DB and in the entity should not be the same. Value in the DB: " + nameFromDb;
				String msgAccessKey = "Access Key in the DB and in the entity should not be the same. Value in the DB: " + nameFromDb;
				assertThat(msgName, application.getName(), not(equalTo(nameFromDb)));
				assertThat(msgAccessKey, application.getAccessKey(), not(equalTo(accessKeyFromDb)));

			}
		});
		entityManager.getTransaction().commit();

	}

	@Test(expected = IllegalStateException.class)
	public void testCompromisedDataIsNotLoaded() {
		entityManager.getTransaction().begin();
		final String accessKey = KeyGenerator.generate();
		final String secretKey = KeyGenerator.generate();
		final Application application = new Application("testCompromisedDataIsNotLoaded", accessKey, secretKey, ApplicationType.REFERENCE_DATA);
		entityManager.persist(application);
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		entityManager.unwrap(Session.class).doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				PreparedStatement pstmt = connection.prepareStatement("update application set applicationType = 'USER_DATA' where id = ?");
				pstmt.setString(1, application.getId());
				int affectedRows = pstmt.executeUpdate();
				assertEquals("Expected to get exactly 1 row updated.", 1, affectedRows);
			}
		});
		entityManager.getTransaction().commit();

		entityManager.clear();

		entityManager.getTransaction().begin();
		entityManager.find(Application.class, application.getId());
		entityManager.getTransaction().commit();
	}


}

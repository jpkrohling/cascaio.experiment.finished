package com.cascaio.appinfo.v1.boundary;

import com.cascaio.appinfo.v1.control.CascaioAppInfoProperties;
import com.cascaio.appinfo.v1.control.JasyptConfigurator;
import com.cascaio.appinfo.v1.entity.Application;
import com.cascaio.appinfo.v1.entity.ApplicationType;
import com.cascaio.security.KeyGenerator;
import com.cascaio.security.TOTP;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * User: jpkrohling
 * Date: 2013-05-11 8:29 PM
 */
public class AppServiceUnitTest {
	private EntityManager entityManager;
	private Logger logger = LoggerFactory.getLogger(AppService.class);
	private AppService appService;
	private String accessKey;
	private String secretKey;
	private Application application;

	@Before
	public void setup() {
		System.setProperty("cascaio.appinfo.jasypt.password", "private-key-for-unit-test");
		System.setProperty("cascaio.appinfo.properties.filepath", "classpath:/cascaio-appinfo.sample.properties");

		logger.debug("Initializing password encryptor");
		JasyptConfigurator configurator = new JasyptConfigurator();
		configurator.properties = new CascaioAppInfoProperties().getProperties();
		configurator.configureJasypt();

		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("appinfo-unittest-pu");
		this.entityManager = entityManagerFactory.createEntityManager();

		entityManager.getTransaction().begin();
		accessKey = KeyGenerator.generate();
		secretKey = KeyGenerator.generate();
		application = new Application("AppServiceUnitTest", accessKey, secretKey, ApplicationType.USER_DATA);
		entityManager.persist(application);
		entityManager.getTransaction().commit();

		appService = new AppService();
		appService.entityManager = entityManager;
		appService.logger = logger;
		appService.passwordDigester = configurator.getPasswordDigester();
	}

	@Test
	public void testAppCanBeFound() {
		Response response = appService.get(accessKey, TOTP.currentTOTPForKey(secretKey), System.currentTimeMillis());
		assertEquals("We expect to get a 200 when finding a match", 200, response.getStatus());
	}

	@Test
	public void testAppCannotBeFound() {
		Response response = appService.get("non-existing-key", TOTP.currentTOTPForKey(secretKey), System.currentTimeMillis());
		assertEquals("We expect to get a 404 when not finding a match", 404, response.getStatus());
	}

	@Test
	public void testWrongToken() {
		Response response = appService.get(accessKey, TOTP.currentTOTPForKey(KeyGenerator.generate()), System.currentTimeMillis());
		assertEquals("We expect to get a 401 when the token is not a match", 401, response.getStatus());
	}

	@Test
	public void testBadRequestWithNulls() {
		Response response = appService.get(null, TOTP.currentTOTPForKey(secretKey), System.currentTimeMillis());
		assertEquals("We expect to get a 400 when sending the access key as null", 400, response.getStatus());

		response = appService.get("", TOTP.currentTOTPForKey(secretKey), System.currentTimeMillis());
		assertEquals("We expect to get a 400 when sending the access key as empty", 400, response.getStatus());

		response = appService.get(accessKey, null, System.currentTimeMillis());
		assertEquals("We expect to get a 400 when sending the secret key as null", 400, response.getStatus());

		response = appService.get(accessKey, "", System.currentTimeMillis());
		assertEquals("We expect to get a 400 when sending the secret key as empty", 400, response.getStatus());
	}

}

package com.cascaio.backend.v1.control;

import com.cascaio.backend.v1.entity.Application;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 4:54 PM
 */
public class CredentialsCheckerInterceptorTest {

	private CredentialsCheckerInterceptor credentialsCheckerInterceptor;

	@Before
	public void setup() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("backend-unittest-pu");

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		UserContainer userContainer = new UserContainer();
		userContainer.entityManager = entityManager;

		CascaioBackendProperties properties = new CascaioBackendProperties();
		properties.logger = LoggerFactory.getLogger(CascaioBackendProperties.class);

		credentialsCheckerInterceptor = new CredentialsCheckerInterceptor();
		credentialsCheckerInterceptor.entityManager = entityManager;
		credentialsCheckerInterceptor.userContainer = userContainer;
		credentialsCheckerInterceptor.properties = properties.getProperties();
		credentialsCheckerInterceptor.logger = LoggerFactory.getLogger(CredentialsCheckerInterceptor.class);
	}

	@Test
	public void testValidReferenceDataAccess() throws Exception {
		Application application = new Application();
		application.setApplicationType("REFERENCE_DATA");
		ServerResponse response = getServerResponse(application, ReferenceDataEndpoint.class);
		assertNull("We were expecting to get not get any credentials problem, but server response wasn't empty.", response);
	}

	@Test
	public void testAppWithUserDataAccessingReferenceData() throws Exception {
		Application application = new Application();
		application.setApplicationType("USER_DATA");
		ServerResponse response = getServerResponse(application, ReferenceDataEndpoint.class);
		assertEquals(403, response.getStatus());
	}

	@Test
	public void testAppWithReferenceDataAccessingUserData() throws Exception {
		Application application = new Application();
		application.setApplicationType("REFERENCE_DATA");
		ServerResponse response = getServerResponse(application, UserDataEndpoint.class);
		assertEquals(403, response.getStatus());
	}

	@Test
	public void testAppWithUserDataShouldSetUserContainer() throws Exception {
		Application application = new Application();
		application.setApplicationType("USER_DATA");
		application.setUser("testAppWithUserDataShouldSetUserContainer");
		ServerResponse response = getServerResponse(application, UserDataEndpoint.class);
		assertNull("We were expecting to get not get any credentials problem, but server response wasn't empty.", response);
		assertEquals(credentialsCheckerInterceptor.userContainer.getUser().getId(), application.getUser());
	}

	private ServerResponse getServerResponse(Application application, Class endpoint) throws Exception {
		ResourceMethod resourceMethod = mock(ResourceMethod.class);
		HttpRequest httpRequest = mock(HttpRequest.class);
		ClientRequest clientRequest = mock(ClientRequest.class, Answers.RETURNS_DEEP_STUBS.get());
		ClientResponse clientResponse = mock(ClientResponse.class);
		HttpHeaders headers = mock(HttpHeaders.class);
		List valuesForKey = mock(List.class);
		List valuesForToken = mock(List.class);

		credentialsCheckerInterceptor.clientRequest = clientRequest;

		when(resourceMethod.getResourceClass()).thenReturn(endpoint);
		when(clientRequest.get(Application.class)).thenReturn(clientResponse);
		when(httpRequest.getHttpHeaders()).thenReturn(headers);
		when(clientResponse.getStatus()).thenReturn(200);
		when(clientResponse.getEntity()).thenReturn(application);
		when(headers.getRequestHeader("X-Cascaio-AccessKey")).thenReturn(valuesForKey);
		when(headers.getRequestHeader("X-Cascaio-Token")).thenReturn(valuesForToken);
		when(valuesForKey.get(0)).thenReturn("fake-access-key");
		when(valuesForToken.get(0)).thenReturn("fake-token");

		return credentialsCheckerInterceptor.preProcess(httpRequest, resourceMethod);
	}

	@RequiresReferenceDataApplication
	private class ReferenceDataEndpoint {

	}

	private class UserDataEndpoint {

	}
}

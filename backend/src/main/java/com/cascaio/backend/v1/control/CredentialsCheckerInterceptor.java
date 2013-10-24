package com.cascaio.backend.v1.control;

import com.cascaio.backend.v1.entity.Application;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Properties;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 11:18 AM
 */
@ServerInterceptor
@RequestScoped
@Provider
@Precedence("SECURITY")
public class CredentialsCheckerInterceptor implements PreProcessInterceptor {
	private static final String USER_DATA_APPLICATION_TYPE = "USER_DATA";
	private static final String REFERENCE_DATA_APPLICATION_TYPE = "REFERENCE_DATA";
	private static final ClientConnectionManager CONNECTION_MANAGER = new PoolingClientConnectionManager();
	private static final HttpClient HTTP_CLIENT = new DefaultHttpClient(CONNECTION_MANAGER);
	private static final ClientExecutor CLIENT_EXECUTOR = new ApacheHttpClient4Executor(HTTP_CLIENT);

	@Inject
	EntityManager entityManager;

	@Inject
	Logger logger;

	@Inject
	UserContainer userContainer;

	@Inject
	Properties properties;

	ClientRequest clientRequest;

	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
		// is the requester trying to access a reference data endpoint, or user data endpoint?
		boolean requiresReferenceDataPermission = false;
		Annotation[] annotations = method.getResourceClass().getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(RequiresReferenceDataApplication.class)) {
				requiresReferenceDataPermission = true;
			}
		}

		Application application = retrieveApplication(request);
		if (null == application) {
			logger.trace("Couldn't determine what is the application in use for this request.");
			return ServerResponse.copyIfNotServerResponse(Response.status(Response.Status.UNAUTHORIZED).build());
		}

		if (requiresReferenceDataPermission) {
			logger.trace("This endpoint requires a reference data endpoint");
			return validateReferenceDataRequest(application);
		} else {
			logger.trace("This endpoint requires an user data endpoint");
			return validateUserDataRequest(application);
		}
	}

	private ServerResponse validateUserDataRequest(Application application) {
		if (USER_DATA_APPLICATION_TYPE.equals(application.getApplicationType())) {
			userContainer.setUser(application.getUser());
			return null;
		}
		logger.trace("Application Type is not CascaioUser Data, and this application is trying to access an CascaioUser Data endpoint.");
		return ServerResponse.copyIfNotServerResponse(Response.status(Response.Status.FORBIDDEN).build());
	}

	private ServerResponse validateReferenceDataRequest(Application application) {
		if (REFERENCE_DATA_APPLICATION_TYPE.equals(application.getApplicationType())) {
			return null;
		}
		logger.trace("Application Type is not Reference Data, and this application is trying to access a Reference Data endpoint.");
		return ServerResponse.copyIfNotServerResponse(Response.status(Response.Status.FORBIDDEN).build());
	}

	private Application retrieveApplication(HttpRequest request) {

		String accessKey = getValueForHeader(request, "X-Cascaio-AccessKey");
		String token = getValueForHeader(request, "X-Cascaio-Token");
		String timestamp = getValueForHeader(request, "X-Cascaio-Timestamp");

		if (null == accessKey || null == token) {
			logger.warn("Access key and/or token were null.");
			return null;
		}

		if (null == timestamp) {
			timestamp = String.valueOf(System.currentTimeMillis());
		}

		ClientRequest req = getClientRequest();
		req
				.header("X-Cascaio-AccessKey", properties.getProperty("cascaio.backend.appinfo.accesskey"))
				.pathParameter("accessKey", accessKey)
				.queryParameter("token", token)
				.queryParameter("time", timestamp);

		ClientResponse<Application> res = null;
		try {
			res = req.get(Application.class);
		} catch (Exception e) {
			logger.error("Exception while trying to get the application.", e);
			return null;
		}

		if (null != res) {
			if (res.getStatus() == 200) {
				return res.getEntity();
			} else {
				logger.warn("Authentication failed. Response code: {}", res.getStatus());
			}

			logger.trace("Releasing the ClientRequest connection");
			res.releaseConnection();
		}

		logger.error("Response's entity is null");
		return null;
	}

	private ClientRequest getClientRequest() {
		if (null == this.clientRequest) {
			this.clientRequest = new ClientRequest(properties.getProperty("cascaio.backend.appinfo.endpoint"), CLIENT_EXECUTOR);
		}
		return this.clientRequest;
	}

	private String getValueForHeader(HttpRequest request, String header) {
		List<String> headers = request.getHttpHeaders().getRequestHeader(header);
		if (null == headers) {
			logger.warn("Header {} was null", header);
			return null;
		}

		if (headers.size() == 0) {
			logger.trace("The header {} was not sent.", header);
			return null;
		}

		String headerValue = headers.get(0);

		if (null == headerValue || headerValue.isEmpty()) {
			logger.warn("Header value for {} was null", header);
			return null;
		}

		return headerValue;
	}
}

package com.cascaio.appinfo.v1.boundary;

import com.cascaio.appinfo.v1.entity.Application;
import com.cascaio.appinfo.v1.entity.ApplicationType;
import com.cascaio.security.KeyGenerator;
import com.cascaio.security.TOTP;
import org.jasypt.digest.StringDigester;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * User: jpkrohling
 * Date: 2013-05-11 12:59 PM
 */
@Stateless
@Path("/applications")
public class AppService {

	@Inject
	EntityManager entityManager;

	@Inject
	Logger logger;

	@Inject
	StringDigester passwordDigester;

	/**
	 * A GET request to this endpoint needs an access key and a token, which should render a valid known application
	 * to our system.
	 *
	 * @param accessKey The application's access key
	 * @param token     The current token, based on the secret key.
	 * @return Either:
	 *         <ul>
	 *         <li>200 OK - With a JSON representation of the application.</li>
	 *         <li>400 Bad Request - In case the access key and/or token is missing.</li>
	 *         <li>401 Unauthorized - In case the token is not valid or has expired.</li>
	 *         <li>404 Not Found - In case the access key is not known to us.</li>
	 *         </ul>
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{accessKey}")
	public Response get(@PathParam("accessKey") String accessKey,
						@QueryParam("token") String token,
						@QueryParam("time") long time) {

		if (null == accessKey || accessKey.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		if (null == token || token.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		Application application = retrieveApplication(accessKey);
		if (null == application) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		if (!isValidTOTP(application.getSecretKey(), token, time)) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		return Response.ok(application).build();
	}

    /**
     * Creates a new application, based on the application name and type. Generates a new accessKey and secretKey
     * for this application, and returns both. The plain access key is available only during this call, so, the caller
     * has to make sure to securely store this information on their side, as this is hashed on our side.
     *
     * @param name The application's name
     * @param applicationType The type of the application, as string. The values are a 1-1 match with the
     *                        enum ApplicationType
     * @return A Bad Request if the name or applicationType is empty, or if the applicationType is not valid as per
     * the enum. Returns a 200 OK with the Application in JSON format if the registration was successful.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@FormParam("name") String name, @FormParam("applicationType") String applicationType) {
        String accessKey = KeyGenerator.generate();
        String secretKey = KeyGenerator.generate();

        if (null == name || name.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (null == applicationType || applicationType.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ApplicationType type = null;
        try {
            type = ApplicationType.valueOf(applicationType);
        } catch (IllegalArgumentException iae) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Application application = new Application(name, accessKey, secretKey, type);

        entityManager.persist(application);

        return Response.ok(application).build();
    }

    /**
     * Determines whether a given token is valid for the given secret key or not. Valid values are the current
     * value for the TOTP and the previous one.
     *
     * @param secretKey The application-specific secret key
     * @param token The TOTP-based token
     * @return true when the token is a valid token for the secret key
     */
	private boolean isValidTOTP(String secretKey, String token, long time) {
		String current = TOTP.currentTOTPForKey(secretKey, time);
		String previous = TOTP.previousTOTPForKey(secretKey, time);

		if (current.equals(token) || previous.equals(token)) {
			return true;
		} else {
			logger.trace(
					"TOTP was not a match: {} was sent, {} or {} were possibilities for timestamp {}",
					new String[]{token, current, previous, String.valueOf(time)}
			);
			return false;
		}
	}

	/**
	 * Retrieves the application data for the given accessKey.
	 *
	 * @param accessKey The Access key of the application to lookup
	 * @return An Application object
	 */
	private Application retrieveApplication(String accessKey) {
		// This is a contention point!!!
		// We are storing the access key as a strong password in the DB, and we can't just check the checksum,
		// as Jasypt's password encryption generates a digest which is not the same every time, but two digests can be
		// used to validate each other. So, we need to retrieve all the applications from the DB and check if the api
		// key they sent matches one of ours from the DB. If it matches, then we select this from the list.
		// This is kinda of OK if the number of applications are small, but this will probably not be acceptable
		// if this list grows large and large. But we can think of a solution in the future. For instance, by caching
		// the list of applications in a 2-Level cache, or by not hashing the public key.
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Application> query = builder.createQuery(Application.class);
		Root<Application> root = query.from(Application.class);
		query.select(root);
		List<Application> applicationList = entityManager.createQuery(query).getResultList();
		Application application = null;
		for (Application a : applicationList) {
			String[] accesskeys = {accessKey, a.getAccessKey()};
			logger.trace("Comparing accesskey {} with this entry from the database: {}", accesskeys);
			if (passwordDigester.matches(accessKey, a.getAccessKey())) {
				application = a;
				break;
			}
		}
		// end of the contention point :-)

		return application;
	}

}

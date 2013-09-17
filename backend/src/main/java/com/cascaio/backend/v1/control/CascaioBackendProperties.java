package com.cascaio.backend.v1.control;

import com.cascaio.properties.PropertiesHelper;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Properties;

/**
 * User: jpkrohling
 * Date: 6/22/13
 * Time: 12:40 PM
 */
@ApplicationScoped
public class CascaioBackendProperties {

	@Inject
	Logger logger;

	private Properties properties;

	public void loadProperties() {
		String decryptionKey = System.getenv("CASCAIO_BACKEND_DECRYPTION_KEY_PROPERTIES");

		// fallback: system properties (like, -Dcascaio.....=bla)
		if (null == decryptionKey || decryptionKey.isEmpty()) {
			decryptionKey = System.getProperty("cascaio.backend.properties.decryptionKey");
		}

		PropertiesHelper helper = new PropertiesHelper();
		String filename = "cascaio-backend.properties";

		// first option: system property, which overrides everything
		String pathToFile = System.getProperty("cascaio.backend.properties.filepath");

		// if we cannot find the file name as a system property, we get it from env var (preferred)
		if (null == pathToFile || pathToFile.isEmpty()) {
			pathToFile = System.getenv("CASCAIO_BACKEND_PROPERTIES_FILE");
			logger.trace("We don't have a system property called cascaio.backend.properties.filepath. ");
			logger.trace("Trying the the env var CASCAIO_BACKEND_PROPERTIES_FILE: {}", pathToFile);
		}

		// if there's no env var defined for this, get the default file from the the data dir
		if (null == pathToFile || pathToFile.isEmpty()) {
			String path = System.getenv("CASCAIO_BACKEND_DATA_DIR");
			pathToFile = path + System.getProperty("file.separator") + filename;
			logger.trace("We didn't have a value for CASCAIO_BACKEND_PROPERTIES_FILE. Trying CASCAIO_BACKEND_DATA_DIR");
		}

		logger.trace("Trying to load the properties from {}", pathToFile);
		// and try to retrieve it... the helper will fall back to filename on the classpath
		properties = helper.retrieveProperties(decryptionKey, pathToFile, filename);
	}

	@Produces
	public Properties getProperties() {
		if (null == properties) {
			loadProperties();
		}
		return this.properties;
	}
}

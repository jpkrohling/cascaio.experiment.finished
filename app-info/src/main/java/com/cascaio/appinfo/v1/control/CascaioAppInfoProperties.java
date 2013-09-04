package com.cascaio.appinfo.v1.control;

import com.cascaio.properties.PropertiesHelper;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import java.util.Properties;

/**
 * User: jpkrohling
 * Date: 6/23/13
 * Time: 10:49 AM
 */
@Startup
@Singleton
public class CascaioAppInfoProperties {
	private Properties properties;

	@PostConstruct
	protected void loadProperties() {
		String decryptionKey = System.getenv("CASCAIO_APPINFO_DECRYPTION_KEY_PROPERTIES");

		// fallback: system properties (like, -Dcascaio.....=bla)
		if (null == decryptionKey || decryptionKey.isEmpty()) {
			decryptionKey = System.getProperty("cascaio.appinfo.properties.decryptionKey");
		}

		PropertiesHelper helper = new PropertiesHelper();
		String filename = "cascaio-appinfo.properties";

		// first option: system property, which overrides everything
		String pathToFile = System.getProperty("cascaio.appinfo.properties.filepath");

		// if we cannot find the file name as a system property, we get it from env var (preferred)
		if (null == pathToFile || pathToFile.isEmpty()) {
			pathToFile = System.getenv("CASCAIO_APPINFO_PROPERTIES_FILE");
		}

		// if there's no env var defined for this, get the default file from the the data dir
		if (null == pathToFile || pathToFile.isEmpty()) {
			String path = System.getenv("CASCAIO_APPINFO_DATA_DIR");

			pathToFile = path + System.getProperty("file.separator") + filename;
		}

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

package com.cascaio.properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jpkrohling
 * Date: 6/23/13
 * Time: 10:25 AM
 */
public class PropertiesHelper {
	private Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);

	public Properties retrieveProperties(String decryptionKey, String pathToFile, String filename) {
		Properties initialProperties = getInitialProperties(decryptionKey, pathToFile, filename);
		Properties properties = new Properties();
		for (Map.Entry<Object, Object> entry : initialProperties.entrySet()) {
			// required, as the value might be encrypted, and decryption occurs on the getProperty
			Object value = resolveEnvVars(initialProperties.getProperty((String) entry.getKey()));
			if (null != logger) {
				logger.trace("Property {} resolves to '{}'", entry.getKey(), value);
			}
			properties.put(entry.getKey(), value);
		}

		return properties;
	}

	private Properties getInitialProperties(String decryptionKey, String pathToFile, String filename) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(decryptionKey);

		Properties initialProperties = new EncryptableProperties(encryptor);

		if (null != logger) {
			logger.trace("Loading properties file");
		}

		InputStream propertiesFileInputStream = null;
		try {
			if (null != pathToFile && !pathToFile.isEmpty()) {
				if (pathToFile.startsWith("classpath:/")) {
					filename = pathToFile.substring("classpath:/".length());
					propertiesFileInputStream = loader.getResourceAsStream(filename);
				} else {
					if (null != logger) {
						logger.trace("Loading properties file from this location: {}", pathToFile);
					}
					propertiesFileInputStream = new FileInputStream(pathToFile);
				}
			} else {
				if (null != logger) {
					logger.trace("Loading properties file from the classpath");
				}
				propertiesFileInputStream = loader.getResourceAsStream(filename);
			}

			initialProperties.load(propertiesFileInputStream);
		} catch (IOException e) {
			if (null != logger) {
				logger.error("Failed to open properties file.");
				throw new RuntimeException(e);
			}
		} finally {
			if (null != propertiesFileInputStream) {
				try {
					propertiesFileInputStream.close();
				} catch (IOException e) {
					if (null != logger) {
						logger.warn("Failed to close the properties file.");
					}
				}
			}
		}

		return initialProperties;
	}

	private Object resolveEnvVars(Object value) {
		if (!(value instanceof String)) {
			return value;
		}

		String input = (String) value;
		// match ${ENV_VAR_NAME} or $ENV_VAR_NAME
		Pattern p = Pattern.compile("\\$\\{(\\w+)\\}|\\$(\\w+)");
		Matcher m = p.matcher(input); // get a matcher object
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
			String envVarValue = System.getenv(envVarName);
			m.appendReplacement(sb, null == envVarValue ? "" : envVarValue);
		}
		m.appendTail(sb);
		return sb.toString();
	}

}

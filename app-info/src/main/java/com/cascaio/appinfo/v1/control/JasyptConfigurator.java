package com.cascaio.appinfo.v1.control;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Properties;

/**
 * User: jpkrohling
 * Date: 2013-05-11 1:19 PM
 */
@Startup
@Singleton
public class JasyptConfigurator {

	private StandardPBEStringEncryptor defaultStringEncryptor;
	private StandardStringDigester passwordDigester;

	@Inject
	public Properties properties;

	@PostConstruct
	public void configureJasypt() {

		if (null == properties) {
			// unfortunately, we might call this from non-managed beans, so, we need a fallback...
			// this totally breaks the purpose of CDI, but we can think of a better solution later
			// TODO: find a reasonable workaround
			properties = new CascaioAppInfoProperties().getProperties();
		}

		// by default, we get from the system's ENV vars
		String password = properties.getProperty("cascaio.appinfo.encryption.password");

		// fallback: system properties (like, -Dcascaio.....=bla)
		if (null == password || password.isEmpty()) {
			password = System.getProperty("cascaio.appinfo.encryption.password");
		}

		// cannot determine the password, fail! we don't want to set an empty password
		if (null == password || password.isEmpty()) {
			throw new RuntimeException("Cannot configure Jasypt, as the encryption password is empty!");
		}

		// TODO: check which algorithms do not need the special JCE export control files
		this.defaultStringEncryptor = new StandardPBEStringEncryptor();
		this.defaultStringEncryptor.setProvider(new BouncyCastleProvider());
		this.defaultStringEncryptor.setAlgorithm(properties.getProperty("cascaio.appinfo.encryption.algorithm"));
		this.defaultStringEncryptor.setKeyObtentionIterations(Integer.valueOf(properties.getProperty("cascaio.appinfo.encryption.iterations")));
		this.defaultStringEncryptor.setPassword(password);

		HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
		registry.registerPBEStringEncryptor("defaultStringEncryptor", defaultStringEncryptor);

		this.passwordDigester = new StandardStringDigester();
		this.passwordDigester.setProvider(new BouncyCastleProvider());
		this.passwordDigester.setAlgorithm(properties.getProperty("cascaio.appinfo.digester.algorithm"));
		this.passwordDigester.setIterations(100000);
		this.passwordDigester.setSaltSizeBytes(16);
		this.passwordDigester.initialize();
	}

	@Produces
	public StandardPBEStringEncryptor getDefaultStringEncryptor() {
		if (null == defaultStringEncryptor) {
			configureJasypt();
		}
		return defaultStringEncryptor;
	}

	@Produces
	public StandardStringDigester getPasswordDigester() {
		if (null == passwordDigester) {
			configureJasypt();
		}

		return passwordDigester;
	}
}

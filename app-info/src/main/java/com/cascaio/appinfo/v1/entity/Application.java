package com.cascaio.appinfo.v1.entity;

import com.cascaio.appinfo.v1.control.CascaioAppInfoProperties;
import com.cascaio.appinfo.v1.control.JasyptConfigurator;
import com.cascaio.security.KeyGenerator;
import org.hibernate.annotations.Type;
import org.jasypt.digest.StringDigester;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Properties;
import java.util.UUID;

/**
 * User: jpkrohling
 * Date: 2013-05-11 1:11 PM
 */
@Entity
public class Application {
	private static Properties properties = new CascaioAppInfoProperties().getProperties();
	private static StringDigester passwordDigester = new JasyptConfigurator().getPasswordDigester();

	@Id
	private String id = UUID.randomUUID().toString();

	@NotNull
	@Type(type = "encryptedString")
	private String name;

	@NotNull
	@XmlTransient
	private String accessKey;

	@NotNull
	@Type(type = "encryptedString")
	@XmlTransient
	private String secretKey;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	private ApplicationType applicationType;

	@Type(type = "encryptedString")
	private String cascaioUser;

	private String salt;

	private String checksum;

	@Transient
	private String plainAccessKey;

	protected Application() {
	}

	public Application(String name, String accessKey, String secretKey, ApplicationType applicationType) {
		this(UUID.randomUUID().toString(), name, accessKey, secretKey, applicationType, null);
	}

	public Application(String name, String accessKey, String secretKey, ApplicationType applicationType, String cascaioUser) {
		this(UUID.randomUUID().toString(), name, accessKey, secretKey, applicationType, cascaioUser);
	}

	public Application(String id, String name, String accessKey, String secretKey, ApplicationType applicationType, String cascaioUser) {
		this.id = id;
		this.name = name;
		this.accessKey = passwordDigester.digest(accessKey);
		this.plainAccessKey = accessKey;
		this.secretKey = secretKey;
		this.applicationType = applicationType;
		this.cascaioUser = cascaioUser;
		this.salt = KeyGenerator.generate();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public ApplicationType getApplicationType() {
		return applicationType;
	}

	public String getCascaioUser() {
		return cascaioUser;
	}

	@PrePersist
	@PreUpdate
	protected void updateChecksum() {
		String newChecksum = recordsChecksum();
		this.checksum = passwordDigester.digest(newChecksum);
	}

	@PostLoad
	protected void checkChecksum() {
		String expectedChecksum = recordsChecksum();
		if (!passwordDigester.matches(expectedChecksum, this.checksum)) {
			throw new IllegalStateException("It seems that this record has been tampered.");
		}
	}

	private String recordsChecksum() {
		return new StringBuilder(this.getId())
				.append("-").append(this.getName())
				.append("-").append(this.salt)
				.append("-").append(this.id)
				.append("-").append(this.applicationType)
				.append("-").append(properties.getProperty("cascaio.appinfo.model.pepper"))
				.toString();
	}

	public String getPlainAccessKey() {
		return plainAccessKey;
	}
}

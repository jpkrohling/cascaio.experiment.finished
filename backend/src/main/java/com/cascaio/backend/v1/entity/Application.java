package com.cascaio.backend.v1.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 2:08 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {
	private String applicationType;
	private String user;

	public Application() {
	}

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}

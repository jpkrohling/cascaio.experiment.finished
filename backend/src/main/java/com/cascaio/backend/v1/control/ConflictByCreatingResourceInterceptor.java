package com.cascaio.backend.v1.control;

import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

/**
 * User: jpkrohling
 * Date: 7/11/13
 * Time: 7:30 PM
 */
public class ConflictByCreatingResourceInterceptor implements PostProcessInterceptor {
	@Override
	public void postProcess(ServerResponse response) {
		if (response.getStatus() == 204) {

		}
	}
}

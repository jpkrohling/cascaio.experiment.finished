package com.cascaio.backend.v1.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.PersistenceContext;

/**
 * User: jpkrohling
 * Date: 7/10/13
 * Time: 9:57 AM
 */
@ApplicationScoped
public class ApplicationResources {

	@Produces
	@PersistenceContext
	private static javax.persistence.EntityManager em;

	@Produces
	public static Logger produceLog(InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
	}
}

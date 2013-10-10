package com.cascaio.appinfo.v1.control;

import com.cascaio.appinfo.v1.entity.Application;
import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import java.util.Map;

/**
 * User: jpkrohling
 * Date: 10/7/13
 * Time: 9:31 AM
 */
@Startup
@Singleton
public class CacheContainerProvider {

	@Resource(lookup="java:jboss/infinispan/container/cascaio")
	private CacheContainer container;
	private Map<String, Application> applicationCache;
	private static final String APP_CACHE_NAME="cascaio-application-cache";

	@PostConstruct()
	public void initializeApplicationCache() {
		applicationCache = container.getCache(APP_CACHE_NAME);
	}

	@Produces
	public Map<String, Application> getApplicationCache() {
		return applicationCache;
	}

	@PreDestroy
	public void cleanUp() {
		container.stop();
		container = null;
	}
}

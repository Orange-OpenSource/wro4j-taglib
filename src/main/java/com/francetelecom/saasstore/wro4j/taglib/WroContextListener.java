package com.francetelecom.saasstore.wro4j.taglib;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application Lifecycle Listener implementation class WroContextListener
 * 
 */
public class WroContextListener implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public WroContextListener() {
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WroConfig.createInstance(sce.getServletContext());
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}

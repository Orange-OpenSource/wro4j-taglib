/*
* Copyright 2011, 2012 France Télécom
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.orange.wro.taglib.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Application Lifecycle Listener implementation class WroContextListener
 * 
 */
public class WroContextListener implements ServletContextListener {
    private final Logger logger = LoggerFactory.getLogger(WroContextListener.class);
    /* package */ static final String ERROR_TEMPLATE = "Exception while {} the wro4j-taglib properties file ({}). Details: {}";

	/**
	 * Default constructor.
	 */
	public WroContextListener() {
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        Context context = new Context(servletContext);

        this.initializeProperties(context);
		WroConfig.createInstance(context);
	}

    private void initializeProperties(Context context) {
        ServletContext servletContext = context.getServletContext();

        Properties properties = new Properties();
        InputStream propertyStream = null;

        try {
            propertyStream = servletContext.getResourceAsStream(context.getPropertiesLocation());

            if (propertyStream != null) {
                properties.load(propertyStream);

                servletContext.setAttribute(
                    Context.WRO_RESOURCE_DOMAIN_ATTRIBUTE,
                    properties.get(Context.WRO_RESOURCE_DOMAIN_ATTRIBUTE)
                );
            }
        } catch (FileNotFoundException fnfEx) {
            logger.warn(ERROR_TEMPLATE, "looking for", context.getPropertiesLocation(), fnfEx.getMessage());
        } catch (IOException ioEx) {
            logger.warn(ERROR_TEMPLATE, "loading", context.getPropertiesLocation(), ioEx.getMessage());
        } finally {
            try {
                if (propertyStream != null) propertyStream.close();
            } catch (IOException ioEx) {
                logger.warn(ERROR_TEMPLATE, "closing", context.getPropertiesLocation(), ioEx.getMessage());
            }
        }
    }

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}
}

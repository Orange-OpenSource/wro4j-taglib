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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * User: atata
 * Date: 17/11/12
 * Time: 02:05
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WroConfig.class, WroContextListener.class, LoggerFactory.class})
public class WroContextListenerTest {
	private static final String PROPERTIES_LOCATION = "wro4j-taglib.temp.properties";
	private static final String RESOURCE_DOMAIN = "//www.fake.domain";

	private ServletContextEvent servletContextEvent;
	private ServletContext servletContext;


	@Before
	public void setUp() {
		this.servletContextEvent = mock(ServletContextEvent.class);
		this.servletContext = mock(ServletContext.class);

		when(this.servletContextEvent.getServletContext()).thenReturn(this.servletContext);
	}

	@After
	public void tearDown() {
		WroConfig.instance = null;
		new File(PROPERTIES_LOCATION).delete();
	}

	@Test(expected = ConfigurationException.class)
	public void contextListenerDoesNotCreateWroConfigInstanceAtCreationTime() {
		WroContextListener contextListener = new WroContextListener();

		WroConfig.getInstance();

		contextListener.contextDestroyed(null);
	}

	@Test
	public void contextListenerCreatesWroConfigInstanceWhenInitialized() throws Exception {
		mockStatic(WroConfig.class);

		WroContextListener contextListener = new WroContextListener();
		contextListener.contextInitialized(this.servletContextEvent);

		verifyStatic();
		WroConfig.createInstance(any(Context.class));
	}

	@Test
	public void contextListenerInformsAboutUnreadableProperties() throws Exception {
		Logger logger = mock(Logger.class);
		mockStatic(LoggerFactory.class);
		when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

		this.createContext();
		this.createProperties(false);

		when(servletContext.getResourceAsStream(anyString())).then(this.getProperties());

		WroContextListener contextListener = new WroContextListener();
		contextListener.contextInitialized(this.servletContextEvent);

		verify(logger).warn(
				eq(WroContextListener.ERROR_TEMPLATE),
				eq("looking for"),
				eq(PROPERTIES_LOCATION),
				anyString()
		);
	}

	@Test
	public void contextListenerSetsPropertiesFromFileToServletContext() throws Exception {
		this.createContext();
		this.createProperties(true);

		when(servletContext.getResourceAsStream(anyString())).then(this.getProperties());

		WroContextListener contextListener = new WroContextListener();
		contextListener.contextInitialized(this.servletContextEvent);

		verify(servletContext).setAttribute(eq(Context.WRO_RESOURCE_DOMAIN_ATTRIBUTE), anyObject());
	}


	/**
	 * Creates a property file in the test location,
	 * adds a property to it and changes the file permissions
	 * depending on the input parameter.
	 *
	 * @param readable
	 */
	private void createProperties(boolean readable) {
		File propertiesFile = new File(PROPERTIES_LOCATION);
		Properties properties = new Properties();
		properties.setProperty(Context.WRO_RESOURCE_DOMAIN_ATTRIBUTE, RESOURCE_DOMAIN);
		FileOutputStream destination = null;

		try {
			destination = new FileOutputStream(propertiesFile);
			properties.store(destination, "");
			propertiesFile.setReadable(readable, false);
		} catch (IOException ioEx) {
			System.out.println(ioEx.getMessage());
		} finally {
			try {
				if (destination != null) destination.close();
			} catch (IOException ioEx) {
				System.out.println(ioEx.getMessage());
			}
		}
	}

	private void createContext() throws Exception {
		Context context = mock(Context.class);

		whenNew(Context.class).withArguments(this.servletContext).thenReturn(context);
		when(context.getPropertiesLocation()).thenReturn(PROPERTIES_LOCATION);
		when(context.getServletContext()).thenReturn(this.servletContext);
	}

	private Answer getProperties() {
		return new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return new FileInputStream(PROPERTIES_LOCATION);
			}
		};
	}
}

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


@RunWith(PowerMockRunner.class)
@PrepareForTest({WroConfig.class, WroContextListener.class, LoggerFactory.class})
public class WroContextListenerTest {
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
		WroConfig.createInstance(any(WroTagLibConfig.class));
	}
}

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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;


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

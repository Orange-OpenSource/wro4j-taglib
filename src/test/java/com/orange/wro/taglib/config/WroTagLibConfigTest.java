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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ro.isdc.wro.http.support.ServletContextAttributeHelper;
import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.factory.WroModelFactory;

import javax.servlet.ServletContext;
import java.util.HashSet;
import java.util.Set;

import static com.orange.wro.taglib.config.WroConfigTestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Angelo Tata
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(WroManager.class)
public class WroTagLibConfigTest {
	private ServletContext servletContext;

	@Before
	public void setUp() {
		this.servletContext = mock(ServletContext.class);

        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL.getKey())).
      			thenReturn(CUSTOM_BASE_URL);

        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL_JS.getKey())).
      			thenReturn(CUSTOM_BASE_URL_JS);

        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL_CSS.getKey())).
      			thenReturn(CUSTOM_BASE_URL_CSS);

		when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.LESS_PATH.getKey())).
			thenReturn(CUSTOM_LESS_PATH);

		when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.RESOURCE_DOMAIN.getKey())).
			thenReturn(CUSTOM_RESOURCE_DOMAIN);
	}

	@Test
	public void configIsLoadedFromInitParamsWhenNoPropertiesPresent() {
		WroTagLibConfig wroTagLibConfig = new WroTagLibConfig(this.servletContext);

		assertEquals("Should be custom base url", CUSTOM_BASE_URL, wroTagLibConfig.getBaseUrl());
		assertEquals("Should be custom base url for JS", CUSTOM_BASE_URL_JS, wroTagLibConfig.getBaseUrlJs());
		assertEquals("Should be custom base url for CSS", CUSTOM_BASE_URL_CSS, wroTagLibConfig.getBaseUrlCss());
		assertEquals("Should be custom Less path", CUSTOM_LESS_PATH, wroTagLibConfig.getLessPath());
		assertEquals("Should be custom resource domain", CUSTOM_RESOURCE_DOMAIN, wroTagLibConfig.getResourceDomain());
	}

	@Test
	public void configIsLoadedFromPropertiesWhenPossible() {
		when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.PROPERTIES_FILENAME.getKey())).
			thenReturn(WroConfigTestConstants.PROPERTIES_TEST_FILENAME);

		WroTagLibConfig wroTagLibConfig = new WroTagLibConfig(this.servletContext);

		assertEquals("Should be classpath base url", CLASSPATH_BASE_URL, wroTagLibConfig.getBaseUrl());
		assertEquals("Should be classpath base url for JS", CLASSPATH_BASE_URL_JS, wroTagLibConfig.getBaseUrlJs());
		assertEquals("Should be classpath base url for CSS", CLASSPATH_BASE_URL_CSS, wroTagLibConfig.getBaseUrlCss());
		assertEquals("Should be classpath Less path", CLASSPATH_LESS_PATH, wroTagLibConfig.getLessPath());
		assertEquals("Should be classpath resource domain", CLASSPATH_RESOURCE_DOMAIN, wroTagLibConfig.getResourceDomain());
	}

	@Test
	public void wroModelIsRetrievedCorrectly() {
		ServletContextAttributeHelper servletContextAttributeHelper = mock(ServletContextAttributeHelper.class);
		WroManagerFactory managerFactory = mock(WroManagerFactory.class);
		WroManager manager = PowerMockito.mock(WroManager.class);
		WroModelFactory modelFactory = mock(WroModelFactory.class);

		WroModel modelFromTest = new WroModel();

		when(servletContextAttributeHelper.getManagerFactory()).thenReturn(managerFactory);
		when(managerFactory.create()).thenReturn(manager);
		when(manager.getModelFactory()).thenReturn(modelFactory);
		when(modelFactory.create()).thenReturn(modelFromTest);

		WroTagLibConfig wroTagLibConfig = new WroTagLibConfig(this.servletContext);
		WroModel modelFromConfig = wroTagLibConfig.getModel(servletContextAttributeHelper);

		assertEquals("Should return the available model", modelFromTest, modelFromConfig);
	}

    @Test
   	public void resourcePathsAreRetrievedFromTheBaseUrl() {
   		WroTagLibConfig wroTagLibConfig = new WroTagLibConfig(this.servletContext);

   		Set<String> testResourcePaths = new HashSet<String>();
   		testResourcePaths.add("testResourcePath1");

   		when(this.servletContext.getResourcePaths(CUSTOM_BASE_URL)).thenReturn(testResourcePaths);

   		assertEquals("Resource paths should be based on the base URL",
   			testResourcePaths, wroTagLibConfig.getResourcePaths()
   		);
   	}

    @Test(expected = ConfigurationException.class)
    public void whenNoBaseUrlIsAvailableExecutionStops() {
        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL.getKey())).
      			thenReturn(null);
        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL_JS.getKey())).
      			thenReturn(null);
        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL_CSS.getKey())).
      			thenReturn(null);

        WroTagLibConfig wroTagLibConfig = new WroTagLibConfig(this.servletContext);
        wroTagLibConfig.getResourcePaths();
    }

    @Test(expected = ConfigurationException.class)
    public void whenOnlyJSBaseUrlIsAvailableExecutionStops() {
        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL.getKey())).
      			thenReturn(null);
        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL_JS.getKey())).
      			thenReturn(null);

        WroTagLibConfig wroTagLibConfig = new WroTagLibConfig(this.servletContext);
        wroTagLibConfig.getResourcePaths();
    }

    @Test(expected = ConfigurationException.class)
    public void whenOnlyCSSBaseUrlIsAvailableExecutionStops() {
        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL.getKey())).
      			thenReturn(null);
        when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL_CSS.getKey())).
      			thenReturn(null);

        WroTagLibConfig wroTagLibConfig = new WroTagLibConfig(this.servletContext);
        wroTagLibConfig.getResourcePaths();
    }


    @Test
   	public void resourcePathsAreRetrievedFromTheJSAndCSSUrlsWhenBaseUrlIsNotAvailable() {
           when(this.servletContext.getInitParameter(WroTagLibConfig.InitParam.BASE_URL.getKey())).
         			thenReturn(null);

   		WroTagLibConfig wroTagLibConfig = new WroTagLibConfig(this.servletContext);

        Set<String> testResourcePathsJs = new HashSet<String>();
      		testResourcePathsJs.add("testResourcePathJS1");

        Set<String> testResourcePathsCss = new HashSet<String>();
      		testResourcePathsCss.add("testResourcePathCSS1");

        Set<String> testResourcePaths = new HashSet<String>();
        testResourcePaths.addAll(testResourcePathsJs);
        testResourcePaths.addAll(testResourcePathsCss);


   		when(this.servletContext.getResourcePaths(CUSTOM_BASE_URL_JS)).thenReturn(testResourcePathsJs);
   		when(this.servletContext.getResourcePaths(CUSTOM_BASE_URL_CSS)).thenReturn(testResourcePathsCss);

   		assertEquals("Resource paths should be based on the base URL",
   			testResourcePaths, wroTagLibConfig.getResourcePaths()
   		);
   	}
}

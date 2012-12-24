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
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ro.isdc.wro.http.support.ServletContextAttributeHelper;
import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

import javax.servlet.ServletContext;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WroConfig.class, WroManager.class})
public class WroConfigTest {
	public static final String TEST_GROUP = "testGroup";
	ServletContext servletContext;
	WroTagLibConfig wroTagLibConfig;

	@Before
	public void setUp() {
		this.servletContext = mock(ServletContext.class);
		this.wroTagLibConfig = mock(WroTagLibConfig.class);
	}

	@After
	public void tearDown() {
		WroConfig.instance = null;
	}

	@Test(expected = ConfigurationException.class)
	public void instanceIsNotAvailableWhenNoInstanceCreatedFirst() {
		WroConfig.getInstance();
	}

	@Test
	public void instanceIsAvailableWhenInstanceCreatedAndModelAvailable() {
		when(this.wroTagLibConfig.getModel(
				Matchers.<ServletContextAttributeHelper>anyObject())
		).thenReturn(new WroModel());

		WroConfig.createInstance(this.wroTagLibConfig);

		assertNotNull("The wro configuration retrieved is null", WroConfig.getInstance());
	}

	@Test
	public void modelIsAccessibleWhenInstanceCreated() {
		when(this.wroTagLibConfig.getModel(
				Matchers.<ServletContextAttributeHelper>anyObject()
		)).thenReturn(this.getModel());

		WroConfig.createInstance(this.wroTagLibConfig);
		FilesGroup testGroup = WroConfig.getInstance().getGroup(TEST_GROUP);

		assertThat("The test group has the wrong number of JS resources",
				testGroup.get(ResourceType.JS).size(),
				is(2));

		assertThat("The test group has the wrong number of CSS resources",
				testGroup.get(ResourceType.CSS).size(),
				is(1));
	}

	private WroModel getModel() {
		WroModel model = new WroModel();
		Group group = new Group(TEST_GROUP);
		Resource js1 = Resource.create("a", ResourceType.JS);
		Resource js2 = Resource.create("b", ResourceType.JS);
		Resource css1 = Resource.create("c", ResourceType.CSS);

		group.setResources(Arrays.asList(js1, js2, css1));
		model.setGroups(Arrays.asList(group));

		return model;
	}
}

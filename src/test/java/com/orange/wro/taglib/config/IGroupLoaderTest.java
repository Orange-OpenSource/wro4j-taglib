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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ro.isdc.wro.model.resource.ResourceType;

/**
 * @author David Bidorff
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(IGroupLoader.class)
public class IGroupLoaderTest {
	
	private String TEST_GROUP = "testGroup";
	
	@Test
	public void explodedFilesAreNotLoadedWhenUnnecessary() {

		IGroupLoader loader = getLoader();
		
		FilesGroup filesGroup = new FilesGroup(TEST_GROUP, loader);
		filesGroup.getMinimizedFile(ResourceType.JS);

		verify(loader, times(0)).getResources(ResourceType.JS);
		verify(loader, times(0)).getResources(ResourceType.CSS);
	}
	
	@Test
	public void explodedJSFilesAreLoadedWhenNecessary() {

		IGroupLoader loader = getLoader();
		
		FilesGroup filesGroup = new FilesGroup(TEST_GROUP, loader);
		filesGroup.get(ResourceType.JS);

		verify(loader, times(1)).getResources(ResourceType.JS);
		verify(loader, times(0)).getResources(ResourceType.CSS);
	}

	@Test
	public void explodedCSSFilesAreLoadedWhenNecessary() {

		IGroupLoader loader = getLoader();
		
		FilesGroup filesGroup = new FilesGroup(TEST_GROUP, loader);
		filesGroup.get(ResourceType.CSS);

		verify(loader, times(0)).getResources(ResourceType.JS);
		verify(loader, times(1)).getResources(ResourceType.CSS);
	}

	@Test
	public void minimizedFilesAreNotLoadedWhenUnnecessary() {

		IGroupLoader loader = getLoader();
		
		FilesGroup filesGroup = new FilesGroup(TEST_GROUP, loader);
		filesGroup.get(ResourceType.CSS);

		verify(loader, times(0)).getMinimizedResources();
	}

	@Test
	public void minimizedFilesAreLoadedWhenNecessary() {

		IGroupLoader loader = getLoader();
		
		FilesGroup filesGroup = new FilesGroup(TEST_GROUP, loader);
		filesGroup.getMinimizedFile(ResourceType.CSS);

		verify(loader, times(1)).getMinimizedResources();
	}

	@Test
	public void explodedCSSFilesAreLoadedOnlyOnce() {

		IGroupLoader loader = getLoader();
		
		FilesGroup filesGroup = new FilesGroup(TEST_GROUP, loader);
		filesGroup.get(ResourceType.CSS);
		filesGroup.get(ResourceType.CSS);

		verify(loader, times(1)).getResources(ResourceType.CSS);
	}

	@Test
	public void explodedJSFilesAreLoadedOnlyOnce() {

		IGroupLoader loader = getLoader();
		
		FilesGroup filesGroup = new FilesGroup(TEST_GROUP, loader);
		filesGroup.get(ResourceType.JS);
		filesGroup.get(ResourceType.JS);

		verify(loader, times(1)).getResources(ResourceType.JS);
	}

	@Test
	public void minimizedFilesAreLoadedOnlyOnce() {

		IGroupLoader loader = getLoader();
		
		FilesGroup filesGroup = new FilesGroup(TEST_GROUP, loader);
		filesGroup.getMinimizedFile(ResourceType.CSS);
		filesGroup.getMinimizedFile(ResourceType.JS);

		verify(loader, times(1)).getMinimizedResources();
	}
	
	private IGroupLoader getLoader() {

		Map<ResourceType, String> minimized = new HashMap<ResourceType, String>();
		minimized.put(ResourceType.CSS, "x");
		minimized.put(ResourceType.JS, "y");
		
		IGroupLoader loader = mock(IGroupLoader.class);
		when(loader.getMinimizedResources()).thenReturn(minimized);
		when(loader.getResources(ResourceType.JS)).thenReturn(Arrays.asList("a", "b"));
		when(loader.getResources(ResourceType.CSS)).thenReturn(Arrays.asList("c"));
		
		return loader;
	}
}

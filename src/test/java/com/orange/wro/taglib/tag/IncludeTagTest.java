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
package com.orange.wro.taglib.tag;

import com.orange.wro.taglib.config.ConfigurationException;
import com.orange.wro.taglib.config.FilesGroup;
import com.orange.wro.taglib.config.WroConfig;
import com.orange.wro.taglib.config.WroTagLibConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ro.isdc.wro.model.resource.ResourceType;

import static com.orange.wro.taglib.tag.WroTagTestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author: Angelo Tata
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WroConfig.class, IncludeTag.class})
public class IncludeTagTest {
	private WroConfig wroConfig;
	private WroTagLibConfig wroTagLibConfig;
	private FilesGroup group;

	private static final String SCRIPT_SINGLE_TAG_OUTPUT_PRETTY = GROUP_FIRST_FILENAME_JS + "\n";
	private static final String SCRIPT_MULTIPLE_TAG_OUTPUT_PRETTY = "testFile1FromGroup1.js" +
			"\n" + "testFile2FromGroup1.js" + "\n";

	@Before
	public void setUp() {
		mockStatic(WroConfig.class);
		wroConfig = mock(WroConfig.class);
		wroTagLibConfig = mock(WroTagLibConfig.class);
		group = mock(FilesGroup.class);

		when(WroConfig.getInstance()).thenReturn(this.wroConfig);
	}

	@Test
	public void failsWhenGroupUnavailable() {
		IncludeTag tag = newIncludeTag();
		tag.setGroupNames(GROUP_NAMES);

		try {
			tag.writeTag(new StringBuilder());
			fail("Expected exception");
		} catch (ConfigurationException ex) {
			assertEquals(
				"Should inform about missing group",
				"group 'testGroupName1' was not found.",
				ex.getMessage()
			);
		}
	}

	@Test
	public void failsWhenMinimizedFilesUnavailable() {
		when(wroConfig.getGroup(GROUP_NAME_FIRST)).thenReturn(new FilesGroup(GROUP_NAME_FIRST));

		IncludeTag tag = newIncludeTag();
		tag.setGroupNames(GROUP_NAMES);

		try {
			tag.writeTag(new StringBuilder());
			fail("Expected exception");
		} catch (ConfigurationException ex) {
			assertEquals(
				"Should inform about missing minimized file",
				"minimized file for group type 'JS' for group 'testGroupName1' was not found.",
				ex.getMessage()
			);
		}
	}

	@Test
	public void failsWhenExplodedFileListUnavailable() {
		when(wroConfig.getGroup(GROUP_NAME_FIRST)).thenReturn(new FilesGroup(GROUP_NAME_FIRST));

		IncludeTag tag = newIncludeTag();
		tag.setGroupNames(GROUP_NAMES);
		tag.setExploded(true);

		try {
			tag.writeTag(new StringBuilder());
			fail("Expected exception");
		} catch (ConfigurationException ex) {
			assertEquals(
				"Should inform about missing minimized file",
				"exploded file list for group type 'JS' for group 'testGroupName1' was not found.",
				ex.getMessage()
			);
		}
	}

	@Test
	public void allowsOutputEditingBeforeAndAfterWriting() throws Exception {
		IncludeTag tag = newIncludeTag();

		StringBuilder output = new StringBuilder();
		tag.writeTag(output);

		verify(tag).writeBegin(output);
		verify(tag).writeEnd(output);
	}

	@Test
	public void addsNewlinesWhenPrettifyingOutput() throws Exception {
		when(wroConfig.getWroTagLibConfig()).thenReturn(wroTagLibConfig);
		when(wroTagLibConfig.getResourceDomain()).thenReturn(TEST_CDN_DOMAIN);
		when(wroConfig.getGroup(GROUP_NAME_FIRST)).thenReturn(group);
		when(group.getMinimizedFile(ResourceType.JS)).thenReturn(GROUP_FIRST_FILENAME_JS);
		when(group.get(ResourceType.JS)).thenReturn(GROUP_FIRST_FILES);

		IncludeTag tag = newIncludeTag();
		doReturn(CONTEXT_PATH).when(tag, "getContextPath");
		tag.setGroupNames(GROUP_NAMES);
		tag.setPretty(true);

		StringBuilder output = new StringBuilder();
		tag.writeTag(output);

		assertEquals("Should output the file name and a newline",
				SCRIPT_SINGLE_TAG_OUTPUT_PRETTY,
				output.toString()
		);

		output.setLength(0);
		tag.setExploded(true);
		tag.writeTag(output);

		assertEquals("Should output the name of each file in the group, each followed by a newline",
				SCRIPT_MULTIPLE_TAG_OUTPUT_PRETTY,
				output.toString()
		);
	}

	private IncludeTag newIncludeTag() {
		return spy(new IncludeTag() {
			@Override
			protected String getMarkupFormat(String src) { return src; }

			@Override
			protected ResourceType getGroupType() { return ResourceType.JS; }

			@Override
			protected String quote(String str) { return str; }
		});
	}
}

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
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author: Angelo Tata
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WroConfig.class, StyleHtmlIncludeTag.class})
public class StyleHtmlIncludeTagTest {
	private static final String STYLE_SINGLE_TAG_OUTPUT = "<link rel='stylesheet' href='//test.cdn.domain/wroTest/testGroupName1.css' />";
	private static final String STYLE_SINGLE_TAG_OUTPUT_FOR_LESS = "<link rel='stylesheet/less' type='text/css' href='//test.cdn.domain/wroTest/testGroupName1.less' />";
	private static final String STYLE_MULTIPLE_TAG_OUTPUT = "<link rel='stylesheet' href='//test.cdn.domain/wroTest/testFile1FromGroup1.css' />" +
			"<link rel='stylesheet' href='//test.cdn.domain/wroTest/testFile2FromGroup1.css' />";

	private WroConfig wroConfig;
	private WroTagLibConfig wroTagLibConfig;
	private FilesGroup group;

	@Before
	public void setUp() {
		mockStatic(WroConfig.class);
		wroConfig = mock(WroConfig.class);
		wroTagLibConfig = mock(WroTagLibConfig.class);
		group = mock(FilesGroup.class);

		when(WroConfig.getInstance()).thenReturn(this.wroConfig);
		when(wroConfig.getWroTagLibConfig()).thenReturn(wroTagLibConfig);
	}


	@Test
	public void shouldBuildASingleStyleTag() throws Exception {
		when(group.getMinimizedFile(ResourceType.CSS)).thenReturn(GROUP_FIRST_FILENAME_CSS);

		StringBuilder output = new StringBuilder();
		this.buildTag(output, false);

		assertEquals("Should use the right context and file name",
			STYLE_SINGLE_TAG_OUTPUT,
			output.toString()
		);
	}

	@Test
	public void shouldBuildASingleStyleTagForLess() throws Exception {
		when(group.getMinimizedFile(ResourceType.CSS)).thenReturn(GROUP_FIRST_FILENAME_LESS);

		StringBuilder output = new StringBuilder();
		this.buildTag(output, false);

		assertEquals("Should use the right context and file name",
			STYLE_SINGLE_TAG_OUTPUT_FOR_LESS,
			output.toString()
		);
	}

	@Test
	public void shouldBuildMultipleStyleTags() throws Exception {
		when(group.get(ResourceType.CSS)).thenReturn(GROUP_FIRST_FILES_CSS);

		StringBuilder output = new StringBuilder();
		this.buildTag(output, true);

		assertEquals("Should use the right context and file name",
			STYLE_MULTIPLE_TAG_OUTPUT,
			output.toString()
		);
	}

	private void buildTag(StringBuilder output, boolean exploded) throws Exception {
		when(wroTagLibConfig.getResourceDomain()).thenReturn(TEST_CDN_DOMAIN);
		when(wroConfig.getGroup(GROUP_NAME_FIRST)).thenReturn(group);

		StyleHtmlIncludeTag tag = spy(new StyleHtmlIncludeTag());
		doReturn(CONTEXT_PATH).when(tag, "getContextPath");

		tag.setGroupNames(GROUP_NAMES);
		tag.setExploded(exploded);
		tag.writeTag(output);
	}
}

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
import static com.orange.wro.taglib.tag.WroTagTestConstants.GROUP_NAMES;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * @author: Angelo Tata
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WroConfig.class, AsJsArrayIncludeTag.class})
public class AsJsArrayIncludeTagTest {
    public static final String JS_ARRAY_MULTIPLE_ELEMENT_OUTPUT_JS = "['\\/\\/test.cdn.domain\\/wroTest\\/testFile1FromGroup1.js','\\/\\/test.cdn.domain\\/wroTest\\/testFile2FromGroup1.js']";
    public static final String JS_ARRAY_MULTIPLE_ELEMENT_OUTPUT_CSS = "['\\/\\/test.cdn.domain\\/wroTest\\/testFile1FromGroup1.css','\\/\\/test.cdn.domain\\/wroTest\\/testFile2FromGroup1.css']";
    public static final String JS_ARRAY_SINGLE_ELEMENT_OUTPUT_JS = "['\\/\\/test.cdn.domain\\/wroTest\\/testGroupName1.js']";
    public static final String JS_ARRAY_SINGLE_ELEMENT_OUTPUT_CSS = "['\\/\\/test.cdn.domain\\/wroTest\\/testGroupName1.css']";
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
   	public void shouldBuildAJSArrayWithOneElement() throws Exception {
   		when(group.getMinimizedFile(ResourceType.JS)).thenReturn(GROUP_FIRST_FILENAME_JS);
   		when(group.getMinimizedFile(ResourceType.CSS)).thenReturn(GROUP_FIRST_FILENAME_CSS);

   		StringBuilder output = new StringBuilder();
   		this.buildTag(output, false, "JS");

        assertEquals("Should use the right context and file name",
                JS_ARRAY_SINGLE_ELEMENT_OUTPUT_JS,
            output.toString()
        );

        output.setLength(0);

        this.buildTag(output, false, "CSS");

        assertEquals("Should use the right context and file name",
                JS_ARRAY_SINGLE_ELEMENT_OUTPUT_CSS,
            output.toString()
        );
   	}

    @Test
   	public void shouldBuildAJSArrayWithMultipleElements() throws Exception {
   		when(group.get(ResourceType.JS)).thenReturn(GROUP_FIRST_FILES_JS);
   		when(group.get(ResourceType.CSS)).thenReturn(GROUP_FIRST_FILES_CSS);

   		StringBuilder output = new StringBuilder();
   		this.buildTag(output, true, "JS");

        assertEquals("Should use the right context and file name",
                JS_ARRAY_MULTIPLE_ELEMENT_OUTPUT_JS,
                output.toString()
        );

        output.setLength(0);

        this.buildTag(output, true, "CSS");

        assertEquals("Should use the right context and file name",
                JS_ARRAY_MULTIPLE_ELEMENT_OUTPUT_CSS,
            output.toString()
        );
   	}

    private void buildTag(StringBuilder output, boolean exploded, String resourceType) throws Exception {
   		when(wroTagLibConfig.getResourceDomain()).thenReturn(TEST_CDN_DOMAIN);
   		when(wroConfig.getGroup(GROUP_NAME_FIRST)).thenReturn(group);

   		AsJsArrayIncludeTag tag = spy(new AsJsArrayIncludeTag());
   		doReturn(CONTEXT_PATH).when(tag, "getContextPath");

   		tag.setGroupNames(GROUP_NAMES);
   		tag.setExploded(exploded);
        tag.setGroupType(resourceType);
   		tag.writeTag(output);
   	}
}

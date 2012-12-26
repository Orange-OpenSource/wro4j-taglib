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

import com.orange.wro.taglib.config.WroConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.orange.wro.taglib.tag.WroTagTestConstants.CONTEXT_PATH;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * @author: Angelo Tata
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({OptionalScriptTag.class})
public class OptionalScriptTagTest {
    public static final String TEST_STRING = "test";
    public static final String SCRIPT_TAG_OUTPUT = "<script src='/wroTest/lessPath' type='text/javascript'></script>";
    private OptionalScriptTag tag;

    @Before
    public void setUp() throws Exception {
        tag = spy(new OptionalScriptTag());
        doReturn(CONTEXT_PATH).when(tag, "getContextPath");
    }
    @Test
    public void outputIsEmptyWhenLessIsNotNeeded() throws Exception {
        doReturn(false).when(tag, "isLessNeeded");

        StringBuilder output = new StringBuilder(TEST_STRING);
        tag.writeTag(output);

        assertEquals("Should contain only the test string",
            TEST_STRING,
            output.toString()
        );
    }

    @Test
    public void outputIsEmptyWhenLessIsNeededButNotConfigured() throws Exception {
        doReturn(true).when(tag, "isLessNeeded");
        doReturn("").when(tag, "getLessPath");

        StringBuilder output = new StringBuilder(TEST_STRING);
        tag.writeTag(output);

        assertEquals("Should contain only the test string",
            TEST_STRING,
            output.toString()
        );
    }

    @Test
    public void shouldBuildAScriptTagForLess() throws Exception {
        doReturn(true).when(tag, "isLessNeeded");
        doReturn("lessPath").when(tag, "getLessPath");

        StringBuilder output = new StringBuilder();
        tag.writeTag(output);

        assertEquals("Should use the right context and file path",
            SCRIPT_TAG_OUTPUT,
            output.toString()
        );
    }
}

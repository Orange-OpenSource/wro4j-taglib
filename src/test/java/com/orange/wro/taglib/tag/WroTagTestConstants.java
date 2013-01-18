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

import java.util.Arrays;
import java.util.List;

/**
 * @author: Angelo Tata
 */
public interface WroTagTestConstants {
	String GROUP_NAMES = "testGroupName1";
	String GROUP_NAME_FIRST = "testGroupName1";
	String GROUP_FIRST_FILENAME_JS = "testGroupName1.js";
	String GROUP_FIRST_FILENAME_CSS = "testGroupName1.css";
	String GROUP_FIRST_FILENAME_LESS = "testGroupName1.less";
	String TEST_CDN_DOMAIN = "//test.cdn.domain";
	String CONTEXT_PATH = "/wroTest/";
	List<String> GROUP_FIRST_FILES_JS = Arrays.asList(new String[]{"testFile1FromGroup1.js", "testFile2FromGroup1.js"});
	List<String> GROUP_FIRST_FILES_CSS = Arrays.asList(new String[]{"testFile1FromGroup1.css", "testFile2FromGroup1.css"});
}

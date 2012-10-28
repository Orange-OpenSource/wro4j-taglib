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

package com.francetelecom.wro.taglib.tag;

import ro.isdc.wro.model.resource.ResourceType;

public class StyleHtmlIncludeTag extends HtmlIncludeTag {
	private static final ResourceType groupType = ResourceType.CSS;
	private static final String markupFormat = "<link rel='stylesheet' href='%s' />";
	
	protected ResourceType getGroupType() {
		return groupType;
	}
	protected String getMarkupFormat() {
		return markupFormat;
	}

}

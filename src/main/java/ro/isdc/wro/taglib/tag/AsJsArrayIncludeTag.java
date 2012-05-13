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

package ro.isdc.wro.taglib.tag;

import org.apache.commons.lang3.StringEscapeUtils;

import ro.isdc.wro.model.resource.ResourceType;

public class AsJsArrayIncludeTag extends IncludeTag {
	private final static String markupFormat = "'%s',";
	private ResourceType groupType;

	public void setGroupType(String groupType) {
		this.groupType = ResourceType.get(groupType);
	}

	protected ResourceType getGroupType() {
		return groupType;
	}

	protected String getMarkupFormat() {
		return markupFormat;
	}

	@Override
	protected void writeBegin(StringBuilder builder) {
		builder.append("[");
	}

	@Override
	protected void writeEnd(StringBuilder builder) {
		int length = builder.length();
		if (length == 0) {
			throw new AssertionError("The builder length is zero. This should not happen as we normally append something at the very beginning.");
		}

		try {
			if (builder.charAt(length - 1) == ',') {
				// will be false if we didn't add any file
				builder.deleteCharAt(length - 1);
			}
		} catch (IndexOutOfBoundsException e) {
			// must not happen as we check before
			throw new AssertionError("deleteCharAt triggered a StringIndexOutOfBoundsException, which should not happen.");
		}
		
		builder.append("]");
	}

	@Override
	protected String quote(String str) {
		return StringEscapeUtils.escapeEcmaScript(str);
	}
}

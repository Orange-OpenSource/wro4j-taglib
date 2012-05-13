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

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import ro.isdc.wro.model.resource.ResourceType;

public class AsJsArrayIncludeTag extends IncludeTag {
	private final static String linkFormat = "'%s',";
	private ResourceType groupType;

	public void setGroupType(String groupType) {
		this.groupType = ResourceType.get(groupType);
	}

	protected ResourceType getGroupType() {
		return groupType;
	}

	protected String getLinkFormat() {
		return linkFormat;
	}

	@Override
	protected void writeBegin(StringBuilder builder) throws IOException {
		builder.append("[");
	}

	@Override
	protected void writeEnd(StringBuilder builder) throws IOException {
		builder.deleteCharAt(builder.length() - 1).append("]");
	}

	@Override
	protected String quote(String str) {
		return StringEscapeUtils.escapeEcmaScript(str);
	}
}

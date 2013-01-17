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
import ro.isdc.wro.model.resource.ResourceType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the base class for all IncludeTags, which contains
 * all business logic. 
 * @author Julien Wajsberg
 */
public abstract class IncludeTag extends SimpleTagSupport {

	private boolean exploded;
	private boolean pretty;
	private List<String> groupNames = new ArrayList<String>();

	@Override
	public final void doTag() throws JspException {
        StringBuilder output = new StringBuilder();

		try {
			writeTag(output);

			getJspContext().getOut().append(output);
		} catch (Exception e) {
			throw new JspException(e);
		}

	}
	
	/*package*/ final void writeTag(StringBuilder output) {
		writeBegin(output);
        writeGroups(output);
        writeEnd(output);
	}

    private void writeGroups(StringBuilder output) {
        WroConfig config = WroConfig.getInstance();

        for (String groupName : groupNames) {

            FilesGroup group = config.getGroup(groupName);
            if (group == null) {
                throw new ConfigurationException("group '" + groupName
                        + "' was not found.");
            }

            if (exploded) {
                includeExploded(output, group);
            } else {
                include(output, group);
            }

        }
    }

    /**
	 * When this parameter is set to true, then the tag
	 * expands to the individual files composing the groups.
	 * 
	 * When it is false (default), the tag expands
	 * to the combined/minimized files for each groups.
	 * 
	 * @param exploded a boolean indicating whether we
	 * want the individual the combined files. 
	 */
	public final void setExploded(boolean exploded) {
		this.exploded = exploded;
	}

	/**
	 * When this parameter is set to true, then the tag
	 * is written with a final cr/lf.
	 * This can makes the output prettier, but this
	 * really depends on your taste.
	 * 
	 * @param pretty a boolean indicating whether we
	 * want a pretty output. 
	 */
	// TODO: makes this a servlet context param ?
	public final void setPretty(boolean pretty) {
		this.pretty = pretty;
	}
	
	/**
	 * Defines the groups to be included at this location.
	 * 
	 * @param strGroupNames Comma-delimited groups.
	 */
	public final void setGroupNames(String strGroupNames) {
		String[] arrGroupNames = strGroupNames.split(",");
		for (String groupName : arrGroupNames) {
			groupNames.add(groupName.trim());
		}
	}

	private void includeExploded(StringBuilder builder, FilesGroup group)
			throws ConfigurationException {
		List<String> files = group.get(getGroupType());
		if (files == null) {
			throw new ConfigurationException(
					"exploded file list for group type '" + getGroupType()
							+ "' for group '" + group.getName()
							+ "' was not found.");
		}

		for (String fileName : files) {
			writeLink(builder, fileName);
			
			if (fileName.endsWith(".less")) {
				getJspContext().setAttribute(WroTagLibConstants.LESS_INJECTED,
						true);
			
			}
		}
	}

	private void include(StringBuilder builder, FilesGroup group)
			throws ConfigurationException {
		String fileName = group.getMinimizedFile(getGroupType());
		if (fileName == null) {
			throw new ConfigurationException("minimized file for group type '"
					+ getGroupType() + "' for group '" + group.getName()
					+ "' was not found.");
		}
		writeLink(builder, fileName);
	}

	private void writeLink(StringBuilder builder, String src) {
		writeLink(builder, src, getMarkupFormat(src));
	}
	
	private void writeLink(StringBuilder builder, String src, String markup) {
		WroConfig config = WroConfig.getInstance();

        String resourceDomain = config.getWroTagLibConfig().getResourceDomain();
        if (resourceDomain == null) resourceDomain = "";

		String contextPath = this.getContextPath();

		String link = String.format(markup, quote(resourceDomain + contextPath + src));

		builder.append(link);

		if (pretty) {
			builder.append('\n');
		}
	}

    private String getContextPath() {
        PageContext context = (PageContext) getJspContext();
        return ((HttpServletRequest) context.getRequest()).getContextPath();
    }

	/**
	 * @return the markup format to generate the markup from the file name.
	 */
	protected abstract String getMarkupFormat(String src);

	/**
	 * @return the resource type for this tag
	 */
	protected abstract ResourceType getGroupType();

	/**
	 * This method should quote the filename depending on the markup
	 * format.
	 * @param str the string to be quoted
	 * @return the quoted string
	 */
	protected abstract String quote(String str);

	/**
	 * Override this if you need to write something at the
	 * beginning of the generated String.
	 * @param builder
	 */
	protected void writeBegin(StringBuilder builder) {

	}

	/**
	 * Override this if you need to modify the generated String
	 * after the generation.
	 * @param builder
	 */
	protected void writeEnd(StringBuilder builder) {

	}
}

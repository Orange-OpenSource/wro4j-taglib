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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.orange.wro.taglib.config.WroTagLibContext;
import org.apache.commons.lang3.StringUtils;

import com.orange.wro.taglib.config.WroConfig;

public class OptionalScriptTag extends SimpleTagSupport {

	@Override
	public void doTag() throws JspException, IOException {

		WroTagLibContext wroTagLibContext = WroConfig.getInstance().getWroTagLibContext();
		PageContext pageContext = (PageContext) getJspContext();
		StringBuilder builder = new StringBuilder();
		
		Object lessObj = pageContext.getAttribute(WroTagLibConstants.LESS_INJECTED);
		boolean isLessNeeded = lessObj == null ? false : (Boolean)lessObj;
		
		if (isLessNeeded) {
			writeScript(pageContext, builder, wroTagLibContext.getLessPath());
		}
		
		pageContext.getOut().append(builder);
	}
	
	private void writeScript(PageContext context, StringBuilder builder,
			String path) throws IOException {
		
		if (!StringUtils.isEmpty(path)) {
			HttpServletRequest req = (HttpServletRequest) context.getRequest();
			String fullPath = req.getContextPath() + path;
			String out = String.format(WroTagLibConstants.JS_MARKUP, fullPath);
			builder.append(out);
		}
	}
}

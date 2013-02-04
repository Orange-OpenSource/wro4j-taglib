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
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class OptionalScriptTag extends SimpleTagSupport {

	@Override
	public void doTag() throws JspException {
		StringBuilder output = new StringBuilder();

        try {
            writeTag(output);
            getJspContext().getOut().append(output);
        } catch (Exception e) {
            throw new JspException(e);
        }
    }

    /*package*/ final void writeTag(StringBuilder output) throws IOException {
        if (isLessNeeded()) {
            writeScript(output);
        }
    }
	
	/*package*/ void writeScript(StringBuilder builder) throws IOException {
        String lessPath = getLessPath();

		if (!StringUtils.isEmpty(lessPath)) {
			String fullPath = getContextPath() + lessPath;
			String script = String.format(WroTagLibConstants.JS_MARKUP, fullPath);
			String out = String.format(WroTagLibConstants.LESS_DEBUG_MARKUP, script);
			builder.append(out);
		}
	}

    private String getLessPath() {
        WroConfig wroConfig = WroConfig.getInstance();

        return wroConfig.getWroTagLibConfig().getLessPath();
    }

    private String getContextPath() {
        PageContext context = (PageContext) getJspContext();
        return ((HttpServletRequest) context.getRequest()).getContextPath();
    }

    private boolean isLessNeeded() {
        PageContext pageContext = (PageContext) getJspContext();
        Object lessObj = pageContext.getAttribute(WroTagLibConstants.LESS_INJECTED);

        return (lessObj == null) ? false : (Boolean)lessObj;
    }
}

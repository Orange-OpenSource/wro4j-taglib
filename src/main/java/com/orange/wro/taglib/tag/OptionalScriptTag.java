package com.orange.wro.taglib.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;

import com.orange.wro.taglib.config.WroConfig;

public class OptionalScriptTag extends SimpleTagSupport {

	@Override
	public void doTag() throws JspException, IOException {

		WroConfig conf = WroConfig.getInstance();
		PageContext context = (PageContext) getJspContext();
		StringBuilder builder = new StringBuilder();
		
		Object lessObj = context.getAttribute(WroTagLibConstants.LESS_INJECTED);
		boolean isLessNeeded = lessObj == null ? false : (Boolean)lessObj;
		
		if (isLessNeeded) {
			writeScript(context, builder, conf.getLessPath());
		}
		
		context.getOut().append(builder);
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

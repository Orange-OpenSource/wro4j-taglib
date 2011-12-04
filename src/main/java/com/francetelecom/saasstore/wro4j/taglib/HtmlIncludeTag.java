package com.francetelecom.saasstore.wro4j.taglib;

public abstract class HtmlIncludeTag extends IncludeTag {
	@Override
	protected String quote(String str) {
		return str; //TODO faire du htmlspecialchars
	}
}

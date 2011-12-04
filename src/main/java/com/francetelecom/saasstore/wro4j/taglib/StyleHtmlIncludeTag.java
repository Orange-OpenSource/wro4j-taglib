package com.francetelecom.saasstore.wro4j.taglib;

public class StyleHtmlIncludeTag extends HtmlIncludeTag {
	private static final String groupType = "css";
	private static final String linkFormat = "<link rel='stylesheet' href='%s' />";
	
	protected String getGroupType() {
		return groupType;
	}
	protected String getLinkFormat() {
		return linkFormat;
	}

}

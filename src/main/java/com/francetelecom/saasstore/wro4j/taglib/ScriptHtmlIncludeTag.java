package com.francetelecom.saasstore.wro4j.taglib;

public class ScriptHtmlIncludeTag extends HtmlIncludeTag {
	private static final String groupType = "js";
	private static final String linkFormat = "<script src='%s'></script>";
	
	protected String getGroupType() {
		return groupType;
	}
	protected String getLinkFormat() {
		return linkFormat;
	}
}

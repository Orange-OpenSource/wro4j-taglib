package com.francetelecom.saasstore.wro4j.taglib;

import java.io.IOException;
import java.util.regex.Pattern;

public class AsJsArrayIncludeTag extends IncludeTag {
	private final static String linkFormat = "'%s',";
	private String groupType;
	private final Pattern quotePattern = Pattern.compile("[\\'\"]");

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	protected String getGroupType() {
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
		return quotePattern.matcher(str).replaceAll("\\$0");
	}
}

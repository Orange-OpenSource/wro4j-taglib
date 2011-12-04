/*
 * This file was taken from Spring 3.0.2, and then modified.
 * Original copyright is :
 * 
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.francetelecom.saasstore.wro4j.taglib;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Package-protected helper class for {@link AntPathMatcher}. Tests whether or
 * not a string matches against a pattern using a regular expression.
 * 
 * <p>
 * The pattern may contain special characters: '*' means zero or more
 * characters; '?' means one and only one character; '{' and '}' indicate a URI
 * template pattern.
 * 
 * @author Arjen Poutsma
 * @since 3.0
 */
public class AntPathStringMatcher {

	private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*\\*?");

	private final Pattern pattern;

	/** Construct a new instance of the <code>AntPatchStringMatcher</code>. */
	public AntPathStringMatcher(String pattern) {
		this.pattern = createPattern(pattern);
	}

	private Pattern createPattern(String pattern) {
		StringBuilder patternBuilder = new StringBuilder();
		Matcher m = GLOB_PATTERN.matcher(pattern);
		int end = 0;
		while (m.find()) {
			patternBuilder.append(quote(pattern, end, m.start()));
			String match = m.group();
			if ("?".equals(match)) {
				patternBuilder.append('.');
			} else if ("**".equals(match)) {
				patternBuilder.append(".*");
			} else if ("*".equals(match)) {
				patternBuilder.append("[^/]*");
			}
			end = m.end();
		}
		patternBuilder.append(quote(pattern, end, pattern.length()));
		return Pattern.compile(patternBuilder.toString());
	}

	private String quote(String s, int start, int end) {
		if (start == end) {
			return "";
		}
		return Pattern.quote(s.substring(start, end));
	}

	/**
	 * Main entry point.
	 * 
	 * @return <code>true</code> if the string matches against the pattern, or
	 *         <code>false</code> otherwise.
	 */
	public boolean match(String str) {
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

}

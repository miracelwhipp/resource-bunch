package com.github.resource.bunch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * A filter is a list of includes and excludes being regular expressions. A string (namely a file name) matches a filter
 * if and only if it does not match any exclude and either matches at least one include or the list of includes is empty.
 *
 * @author jschwarz
 */
public class Filter {

	public static final Filter ALL = new Filter(Collections.<String>emptyList(), Collections.<String>emptyList());

	public static final String QUOTED_FILE_SEPARATOR = (File.separator.equals("\\")) ? "\\\\" : "/";

	private final List<String> includes;
	private final List<String> excludes;

	private Filter(List<String> includes, List<String> excludes) {
		this.includes = includes;
		this.excludes = excludes;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public static Filter newInstance(List<String> includes, List<String> excludes) {

		return new Filter(includes, excludes);
	}

	public static Filter fromAntPattern(List<String> includes, List<String> excludes) {

		List<String> usedIncludes = translateAntPatternToRegex(includes);
		List<String> usedExcludes = translateAntPatternToRegex(excludes);

		return new Filter(usedIncludes, usedExcludes);

	}

	public static List<String> translateAntPatternToRegex(List<String> includes) {

		ArrayList<String> result = new ArrayList<>(includes.size());

		for (String include : includes) {

			result.add(translateAntPatternToRegex(include));
		}

		return result;
	}

	public static String translateAntPatternToRegex(String include) {

		String result = include;

		result = result.replaceAll("\\\\|/", QUOTED_FILE_SEPARATOR);
		result = Pattern.quote(result);
		result = result.replaceAll(Pattern.quote("**"), "\\\\E?\\\\Q");
		result = result.replaceAll(Pattern.quote("*"), "\\\\E[^" + QUOTED_FILE_SEPARATOR + QUOTED_FILE_SEPARATOR + "]*\\\\Q");
		result = result.replaceAll(Pattern.quote("\\E?\\Q"), "\\\\E.*\\\\Q");

		while (result.startsWith("\\Q\\E")) {

			result = result.substring(4);
		}

		while (result.endsWith("\\Q\\E")) {

			result = result.substring(0, result.length() - 4);
		}

		if (result.startsWith("\\E")) {

			result = result.substring(2);
		}

		if (result.endsWith("\\Q")) {

			result += result.substring(0, result.length() - 2);
		}

		return result;
	}

	public Filter append(Filter filter) {

		List<String> newIncludes = new ArrayList<>(includes);
		newIncludes.addAll(filter.getIncludes());

		List<String> newExcludes = new ArrayList<>(excludes);
		newExcludes.addAll(filter.getExcludes());

		return new Filter(newIncludes, newExcludes);
	}

	public boolean matches(String text) {

		if (!included(text)) {

			return false;
		}

		return !excluded(text);
	}

	private boolean excluded(String text) {

		if (excludes.isEmpty()) {

			return false;
		}

		return regexListMatches(excludes, text);
	}

	private boolean included(String text) {

		if (includes.isEmpty()) {

			return true;
		}

		return regexListMatches(includes, text);
	}

	private boolean regexListMatches(List<String> list, String text) {

		for (String include : list) {

			if (text.matches(include)) {

				return true;
			}
		}

		return false;
	}


}

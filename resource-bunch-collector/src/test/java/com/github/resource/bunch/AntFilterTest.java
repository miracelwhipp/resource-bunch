package com.github.resource.bunch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.miracelwhipp.resource.bunch.collector.Filter;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 *
 * This test tests translation from Ant pattern language to regular expression.
 *
 * @author jschwarz
 */
public class AntFilterTest {

	private final String antPattern;
	private final List<String> expectedMatches;
	private final List<String> expectedNonMatches;

	@Factory(dataProvider = "antPatterns")
	public AntFilterTest(String antPattern, List<String> expectedMatches, List<String> expectedNonMatches) {
		this.antPattern = antPattern;
		this.expectedMatches = expectedMatches;
		this.expectedNonMatches = expectedNonMatches;
	}

	@DataProvider
	public static Object[][] antPatterns() {

		return new Object[][]{
				new Object[]{"somethingSimple", Arrays.asList("somethingSimple"), Arrays.asList("", ".*", "nothing.special")},
				new Object[]{"something.Simple", Arrays.asList("something.Simple"), Arrays.asList("", "somethinglSimple", "nothing.special")},
				new Object[]{"*\\foo\\*", Arrays.asList(
						"something" + File.separator + "foo" + File.separator + "else",
						File.separator + "foo" + File.separator + "else",
						"d" + File.separator + "foo" + File.separator,
						"d" + File.separator + "foo" + File.separator + "d"
				), Arrays.asList(
						"mango",
						"foo",
						"foo" + File.separator + "else",
						"d" + File.separator + "foo",
						"d" + File.separator + "d" + File.separator + "foo"
				)},
				new Object[]{"**/bar", Arrays.asList(
						"foo" + File.separator + "bar",
						"foo" + File.separator + "bar" + File.separator + "foo" + File.separator + "bar",
						"foo" + File.separator + "foo" + File.separator + "bar"
				), Arrays.asList(
						"bar",
						"foo" + File.separator + "bar" + File.separator,
						"foo" + File.separator + "bar" + File.separator + "foo" + File.separator + "bar" + File.separator + "foo",
						"foo" + File.separator + "foo" + File.separator + "bar" + File.separator + "foo"
				)},
				new Object[]{"baz/**", Arrays.asList(
						"baz" + File.separator + "bar",
						"baz" + File.separator + "bar" + File.separator + "foo" + File.separator + "bar",
						"baz" + File.separator + "foo" + File.separator + "bar"

				), Arrays.asList(
						"baz",
						"foo" + File.separator + "baz" + File.separator,
						"foo" + File.separator + "baz" + File.separator + "foo" + File.separator + "bar" + File.separator + "foo",
						"foo" + File.separator + "foo" + File.separator + "baz" + File.separator + "foo"
				)},
				new Object[]{"baz**", Arrays.asList(
						"baz" + File.separator + "bar",
						"baz" + File.separator + "bar" + File.separator + "foo" + File.separator + "bar",
						"baz" + File.separator + "foo" + File.separator + "bar",
						"bazbar",
						"bazbar" + File.separator + "foo" + File.separator + "bar",
						"bazfoo" + File.separator + "bar"
				), Arrays.asList(
						"bar",
						"foo" + File.separator + "bazfoo" + File.separator,
						"foo" + File.separator + "bazfoo" + File.separator + "foo" + File.separator + "bar" + File.separator + "foo",
						"foo" + File.separator + "foo" + File.separator + "bazfoo" + File.separator + "foo"
				)},
				new Object[]{"some-[dfg]irectory", Arrays.asList(
						"some-[dfg]irectory"
				), Arrays.asList(
						"some-directory",
						"some-firectory",
						"some-girectory"
				)},
		};
	}

	@Test
	public void testTranslation() {

		String regex = Filter.translateAntPatternToRegex(antPattern);

		List<String> matches = new ArrayList<>(expectedMatches.size());

		for (String expectedMatch : expectedMatches) {

			if (expectedMatch.matches(regex)) {

				matches.add(expectedMatch);
			}
		}

		Assert.assertEquals(listToString(matches), listToString(expectedMatches), "wrong matches for " + regex + "\n");

		List<String> nonMatches = new ArrayList<>(expectedNonMatches.size());

		for (String expectedNonMatch : expectedNonMatches) {

			if (!expectedNonMatch.matches(regex)) {

				nonMatches.add(expectedNonMatch);
			}
		}

		Assert.assertEquals(listToString(nonMatches), listToString(expectedNonMatches), "wrong non-matches for " + regex + "\n");

	}

	private static String listToString(List<String> strings) {

		StringBuilder result = new StringBuilder();


		for (String string : strings) {

			result.append(string).append("\n");
		}

		return result.toString();
	}

}

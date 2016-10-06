package com.github.resource.bunch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * This utility class provides functionality to extract resource bunches from the classpath.
 *
 * @author miracelwhipp
 */
public final class ResourceBunchExtractor {

	public static final String DEFAULT_BUNCH_DESCRIPTOR = "resource.bunch.properties";
	public static final String RESOURCE_SEPARATOR = ",";
	public static final int BUFFER_SIZE = 128 * 1024;

	private ResourceBunchExtractor() {
	}

	public static void extract(String bunchName, File targetDirectory) throws IOException {

		extract(DEFAULT_BUNCH_DESCRIPTOR, bunchName, targetDirectory, bunchName + "/");
	}

	public static void extract(String bunchName, File targetDirectory, String resourcePrefix) throws IOException {

		extract(DEFAULT_BUNCH_DESCRIPTOR, bunchName, targetDirectory, resourcePrefix);
	}

	public static void extract(String descriptorName, String bunchName, File targetDirectory) throws IOException {

		extract(descriptorName, bunchName, targetDirectory, bunchName + "/");
	}

	public static void extract(
			String descriptorName, String bunchName, File targetDirectory, String resourcePrefix) throws IOException {

		if (resourcePrefix == null) {

			resourcePrefix = "";
		}

		if (!resourcePrefix.isEmpty() && !resourcePrefix.endsWith("/")) {

			resourcePrefix = resourcePrefix + "/";
		}

		Enumeration<URL> resources = ResourceBunchExtractor.class.getClassLoader().getResources(descriptorName);

		List<String> resourcesToExtract = new ArrayList<>();

		while (resources.hasMoreElements()) {

			URL resource = resources.nextElement();

			Properties properties = new Properties();

			try (InputStream source = resource.openStream()) {

				properties.load(source);
			}

			String resourceListString = properties.getProperty(bunchName);

			if (resourceListString == null) {

				continue;
			}

			resourcesToExtract.addAll(Arrays.asList(resourceListString.split(RESOURCE_SEPARATOR)));
		}


		for (String resourceToExtract : resourcesToExtract) {

			if (!resourceToExtract.startsWith(resourcePrefix)) {

				continue;
			}

			File targetFile = new File(targetDirectory, resourceToExtract.substring(resourcePrefix.length()));

			targetFile.getParentFile().mkdirs();

			try (
					OutputStream target = new FileOutputStream(targetFile);
					InputStream source =
							ResourceBunchExtractor.class.getClassLoader().getResourceAsStream(resourceToExtract)
			) {

				byte[] buffer = new byte[BUFFER_SIZE];

				int length = 0;

				while ((length = source.read(buffer)) > 0) {

					target.write(buffer, 0, length);
				}
			}
		}
	}


}

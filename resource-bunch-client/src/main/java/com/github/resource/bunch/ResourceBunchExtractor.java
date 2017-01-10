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

	private static final int BUFFER_SIZE = 128 * 1024;
	private static final ResourceBunchExtractor DEFAULT_EXTRACTOR =
			newInstance(ResourceBunchMetadata.DEFAULT_BUNCH_DESCRIPTOR, ResourceBunchExtractor.class.getClassLoader());

	private final String bunchDescriptor;
	private final ClassLoader classLoader;

	private ResourceBunchExtractor(String bunchDescriptor, ClassLoader classLoader) {
		this.bunchDescriptor = bunchDescriptor;
		this.classLoader = classLoader;
	}

	public void extractResource(String bunchName, File targetDirectory) throws IOException {

		extractResource(bunchName, targetDirectory, bunchName + "/");
	}

	public void extractResource(String bunchName, File targetDirectory, String resourcePrefix) throws IOException {

		if (resourcePrefix == null) {

			resourcePrefix = "";
		}

		if (!resourcePrefix.isEmpty() && !resourcePrefix.endsWith("/")) {

			resourcePrefix = resourcePrefix + "/";
		}

		resourcePrefix = bunchName + "/" + resourcePrefix;

		Enumeration<URL> resources = classLoader.getResources(bunchDescriptor);

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

			resourcesToExtract.addAll(Arrays.asList(resourceListString.split(ResourceBunchMetadata.RESOURCE_SEPARATOR)));
		}


		for (String resourceToExtract : resourcesToExtract) {

			if (!resourceToExtract.startsWith(resourcePrefix)) {

				continue;
			}

			File targetFile = new File(targetDirectory, resourceToExtract.substring(resourcePrefix.length()));

			createDirectory(targetFile.getParentFile());

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

	private void createDirectory(File parentFile) throws IOException {

		if (parentFile.exists()) {

			if (parentFile.isDirectory()) {

				return;
			}

			throw new IOException("extraction directory exists and is a file: " + parentFile.getAbsolutePath());
		}

		if (!parentFile.mkdirs()) {

			throw new IOException("unable to create directory " + parentFile.getAbsolutePath());
		}
	}

	public static ResourceBunchExtractor newInstance(String bunchDescriptor, ClassLoader classLoader) {

		return new ResourceBunchExtractor(bunchDescriptor, classLoader);
	}

	public static void extract(String bunchName, File targetDirectory) throws IOException {

		DEFAULT_EXTRACTOR.extractResource(bunchName, targetDirectory);
	}

	public static void extract(String bunchName, File targetDirectory, String resourcePrefix) throws IOException {

		DEFAULT_EXTRACTOR.extractResource(bunchName, targetDirectory, resourcePrefix);
	}

	public static void extract(String descriptorName, String bunchName, File targetDirectory) throws IOException {

		newInstance(descriptorName, ResourceBunchExtractor.class.getClassLoader()).
				extractResource(bunchName, targetDirectory);
	}

	public static void extract(
			String descriptorName, String bunchName, File targetDirectory, String resourcePrefix) throws IOException {

		newInstance(descriptorName, ResourceBunchExtractor.class.getClassLoader()).
				extractResource(bunchName, targetDirectory, resourcePrefix);
	}


}

package com.github.resource.bunch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * This class is capable of scanning a directory for resources and collecting them and their names.
 *
 * @author jschwarz
 */
public class ResourceCollector {

	private final List<ResourceCollection> collections;
	private final Map<String, String> properties = new HashMap<>();

	public ResourceCollector(List<ResourceCollection> collections) {
		this.collections = collections;
	}

	public void addProperty(String name, String value) {

		properties.put(name, value);
	}

	public void addProperties(Map<String, String> properties) {

		this.properties.putAll(properties);
	}

	public File collect(String descriptorName, File targetPath) throws IOException {

		Properties properties = new Properties();

		for (ResourceCollection collection : collections) {

			List<String> resources = collection.collect(targetPath);

			properties.setProperty(collection.getName(), buildResourceString(resources));
		}

		File result = new File(targetPath, descriptorName);

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(result), StandardCharsets.UTF_8)) {

			properties.store(writer, "");
		}

		return result;
	}

	public static ResourceScanner scan() {

		return new ResourceScanner();
	}

	private String buildResourceString(List<String> resources) {

		StringBuilder builder = new StringBuilder(resources.size() * 513);

		boolean first = true;

		for (String resource : resources) {

			if (first) {

				first = false;

			} else {

				builder.append(ResourceBunchMetadata.RESOURCE_SEPARATOR);
			}

			builder.append(resource);
		}

		return builder.toString();
	}

}

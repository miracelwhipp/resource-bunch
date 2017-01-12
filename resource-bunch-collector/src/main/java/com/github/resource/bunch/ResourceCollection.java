package com.github.resource.bunch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * This immutable holds the name of and references to a resource bunch.
 *
 * @author jschwarz
 */
public class ResourceCollection {

	private static final int BUFFER_SIZE = 128 * 1024;

	private final String name;
	private final List<String> resources;
	private final File rootDirectory;

	private ResourceCollection(String name, List<String> resources, File rootDirectory) {
		this.name = name;
		this.resources = resources;
		this.rootDirectory = rootDirectory;
	}

	public String getName() {
		return name;
	}

	public List<String> getResources() {
		return resources;
	}

	public File getRootDirectory() {
		return rootDirectory;
	}


	public List<String> collect(File targetPath) throws IOException {

		ArrayList<String> result = new ArrayList<>(resources.size());

		File subDirectory = new File(targetPath, name);

		createDirectory(subDirectory);

		for (String resource : resources) {

			copyFile(new File(rootDirectory, resource), new File(subDirectory, resource));
			result.add(resourceName(name + "/" + resource));
		}

		return result;
	}

	private static void copyFile(File sourceFileName, File targetFileName) throws IOException {

		createDirectory(targetFileName.getParentFile());

		try (InputStream input = new FileInputStream(sourceFileName);
			 OutputStream target = new FileOutputStream(targetFileName)) {

			byte[] buffer = new byte[BUFFER_SIZE];

			int length = 0;

			while ((length = input.read(buffer)) > 0) {

				target.write(buffer, 0, length);
			}
		}
	}


	private static void createDirectory(File directory) throws IOException {

		if (directory.exists()) {

			if (!directory.isDirectory()) {

				throw new IOException("directory to create is a file: " + directory.getAbsolutePath());
			}

			return;
		}

		if (!directory.mkdirs()) {

			throw new IOException("unable to create directory " + directory.getAbsolutePath());
		}
	}

	public static ResourceCollection scanResource(String collectionName, File resource, Filter filter) {

		if (!resource.exists()) {

			return emptyResource(collectionName, resource);
		}

		List<String> resources = new ArrayList<>();

		if (resource.isDirectory()) {

			File[] subFiles = resource.listFiles();

			if (subFiles == null) {

				return emptyResource(collectionName, resource);
			}

			for (File subFile : subFiles) {

				storeBunch(subFile, resource.getAbsoluteFile().toPath(), filter, resources);
			}


		} else {

			storeBunch(resource, resource.getParentFile().getAbsoluteFile().toPath(), filter, resources);
		}


		return new ResourceCollection(collectionName, resources, resource);
	}

	private static void storeBunch(File resource, Path rootDirectory, Filter filter, List<String> resources) {

		if (!resource.isDirectory()) {

			if (filter.matches(resource.toString())) {

				resources.add(rootDirectory.relativize(resource.getAbsoluteFile().toPath()).toString());
			}


			return;
		}


		File[] subFiles = resource.listFiles();

		if (subFiles == null) {

			return;
		}

		for (File subFile : subFiles) {

			storeBunch(subFile, rootDirectory, filter, resources);
		}

	}

	private static String resourceName(String name) {

		if (name.startsWith("/") || name.startsWith("\\")) {

			name = name.substring(1);
		}

		name = name.replaceAll("\\\\", "/");

		return name;
	}


	private static ResourceCollection emptyResource(String collectionName, File resource) {

		return new ResourceCollection(collectionName, Collections.<String>emptyList(), resource);
	}

}

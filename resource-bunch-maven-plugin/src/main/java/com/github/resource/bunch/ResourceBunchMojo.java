package com.github.resource.bunch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * This goal packs the specified resource files to the output directory and lists their names in a properties file.
 *
 * @author miracelwhipp
 */
@Mojo(name = "create", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class ResourceBunchMojo extends AbstractMojo {

	public static final int BUFFER_SIZE = 128 * 1024;

	@Parameter(required = true)
	private Map<String, String> resources;

	@Parameter(defaultValue = "resource.bunch.properties", property = "resource.bunch.descriptor")
	private String bunchDescriptorResource;

	@Parameter(property = "project.build.outputDirectory", readonly = true)
	private File outputDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		Map<String, List<String>> bunches = new HashMap<>();

		for (Map.Entry<String, String> entry : resources.entrySet()) {

			getLog().debug("found bundle " + entry.getKey());

			List<String> resources = new ArrayList<>();

			bunches.put(entry.getKey(), resources);

			File resource = new File(entry.getValue());

			if (!resource.exists()) {

				getLog().warn("resource does not exist " + entry.getKey() + " (" + resource.getAbsolutePath() + ") - will be ignored.");

				continue;
			}

			File targetDirectory = new File(outputDirectory, entry.getKey());

			if (resource.isDirectory()) {

				File[] subFiles = resource.listFiles();

				if (subFiles == null) {

					return;
				}

				for (File subFile : subFiles) {

					storeBunch(subFile, targetDirectory, resources);
				}


			} else {

				storeBunch(resource, targetDirectory, resources);
			}

		}

		Properties bunchDescriptor = new Properties();

		for (Map.Entry<String, List<String>> entry : bunches.entrySet()) {

			StringBuilder builder = new StringBuilder();

			boolean first = true;

			for (String resourcePart : entry.getValue()) {

				if (first) {

					first = false;

				} else {

					builder.append(",");
				}

				builder.append(resourcePart);
			}

			bunchDescriptor.setProperty(entry.getKey(), builder.toString());
		}

		try (OutputStream target =
					 new FileOutputStream(new File(outputDirectory, bunchDescriptorResource))) {

			bunchDescriptor.store(target, null);

		} catch (IOException e) {

			throw new MojoFailureException(e.getMessage(), e);
		}
	}

	private void storeBunch(File resource, File targetDirectory, List<String> resources)
			throws MojoFailureException {

		if (!resource.isDirectory()) {

			resources.add(resourceName(copyFile(resource, targetDirectory)));

			return;
		}


		File[] subFiles = resource.listFiles();

		if (subFiles == null) {

			return;
		}

		for (File subFile : subFiles) {

			storeBunch(subFile, new File(targetDirectory, resource.getName()), resources);
		}

	}

	private String resourceName(File file) {

		String absoluteFile = file.getAbsolutePath();
		String absoluteOutputDir = outputDirectory.getAbsolutePath();

		if (!absoluteFile.startsWith(absoluteOutputDir)) {

			throw new IllegalStateException("wrong resource " + absoluteFile + " directory " + absoluteOutputDir);
		}

		String result = absoluteFile.substring(absoluteOutputDir.length());

		if (result.startsWith("/") || result.startsWith("\\")) {

			result = result.substring(1);
		}

		result = result.replaceAll("\\\\", "/");

		return result;
	}

	private static File copyFile(File resource, File targetDirectory) throws MojoFailureException {

		if (!targetDirectory.exists()) {

			if (!targetDirectory.mkdirs()) {

				throw new MojoFailureException("unable to create directory " + targetDirectory.getAbsolutePath());
			}
		}

		if (targetDirectory.isFile()) {

			throw new MojoFailureException("directory is a file " + targetDirectory.getAbsolutePath());
		}


		File targetFileName = new File(targetDirectory, resource.getName());

		try (InputStream input = new FileInputStream(resource);
			 OutputStream target = new FileOutputStream(targetFileName)) {

			byte[] buffer = new byte[BUFFER_SIZE];

			int length = 0;

			while ((length = input.read(buffer)) > 0) {

				target.write(buffer, 0, length);
			}

		} catch (IOException e) {

			throw new MojoFailureException(e.getMessage(), e);
		}

		return targetFileName;
	}
}

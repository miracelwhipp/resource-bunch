package io.github.miracelwhipp.resource.bunch.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.miracelwhipp.resource.bunch.collector.Filter;
import io.github.miracelwhipp.resource.bunch.collector.ResourceCollector;
import io.github.miracelwhipp.resource.bunch.collector.ResourceScanner;
import io.github.miracelwhipp.resource.bunch.common.ResourceBunchMetadata;
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

	@Parameter(required = true)
	private Map<String, String> resources;

	@Parameter(defaultValue = ResourceBunchMetadata.DEFAULT_BUNCH_DESCRIPTOR, property = "resource.bunch.descriptor")
	private String bunchDescriptorResource;

	@Parameter(property = "project.build.outputDirectory", readonly = true)
	private File outputDirectory;

	@Parameter
	private List<String> includes;

	@Parameter
	private List<String> excludes;

	/**
	 * This parameter adds custom meta data to the bunch created.
	 */
	@Parameter
	private Map<String, String> properties;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		if (includes == null) {

			includes = Collections.emptyList();
		}

		if (excludes == null) {

			excludes = Collections.emptyList();
		}

		ResourceScanner scanner = ResourceCollector.scan();

		Filter filter = Filter.fromAntPattern(includes, excludes);

		for (Map.Entry<String, String> entry : resources.entrySet()) {

			getLog().debug("found bundle " + entry.getKey());

			File resource = new File(entry.getValue());

			if (!resource.exists()) {

				getLog().warn("resource does not exist " + entry.getKey() + " (" + resource.getAbsolutePath() + ") - will be ignored.");

				continue;
			}

			scanner.scan(entry.getKey(), resource, filter);
		}

		ResourceCollector collector = scanner.newCollector();

		try {

			collector.collect(bunchDescriptorResource, outputDirectory);

		} catch (IOException e) {

			throw new MojoFailureException("unable to collect resource bunches", e);
		}
	}
}

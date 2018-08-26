package io.github.miracelwhipp.resource.bunch.collector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This builder builder {@link ResourceCollector resource collectors} by scanning given directories.
 *
 * @author jschwarz
 */
public class ResourceScanner {

	private List<ResourceCollection> collections = new ArrayList<>();

	public ResourceScanner scan(String name, File resource) {

		return scan(name, resource, Filter.ALL);
	}

	public ResourceScanner scan(String name, File resource, Filter filter) {

		collections.add(ResourceCollection.scanResource(name, resource, filter));

		return this;
	}

	public ResourceCollector newCollector() {

		return new ResourceCollector(new ArrayList<>(collections));
	}


}

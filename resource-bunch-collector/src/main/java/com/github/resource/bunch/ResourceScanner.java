package com.github.resource.bunch;

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

		collections.add(ResourceCollection.scanResource(name, resource));

		return this;
	}

	public ResourceCollector newCollector() {

		return new ResourceCollector(new ArrayList<>(collections));
	}


}

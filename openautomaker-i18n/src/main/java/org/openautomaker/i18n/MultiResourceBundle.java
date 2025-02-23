package org.openautomaker.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MultiResourceBundle extends ResourceBundle {

	private final List<ResourceBundle> delegates;

	public MultiResourceBundle(ResourceBundle... resourceBundles) {
		this.delegates = resourceBundles == null ? new ArrayList<>() : List.of(resourceBundles);
	}

	@Override
	protected Object handleGetObject(String key) {
		Optional<Object> firstPropertyValue = this.delegates.stream()
				.filter(delegate -> delegate != null && delegate.containsKey(key))
				.map(delegate -> delegate.getObject(key))
				.findFirst();

		return firstPropertyValue.isPresent() ? firstPropertyValue.get() : null;
	}

	@Override
	public Enumeration<String> getKeys() {
		List<String> keys = this.delegates.stream()
				.filter(delegate -> delegate != null)
				.flatMap(delegate -> Collections.list(delegate.getKeys()).stream())
				.collect(Collectors.toList());

		return Collections.enumeration(keys);
	}
}
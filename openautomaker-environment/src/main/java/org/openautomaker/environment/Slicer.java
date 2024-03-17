package org.openautomaker.environment;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public enum Slicer {

	CURA("Cura", Paths.get("Cura")),
	CURA_4("Cura 4", Paths.get("Cura4")),
	CURA_5("Cura 5 (Experimental)", Paths.get("Cura5"));

	private final String friendlyName;
	private final Path pathModifier;

	private Slicer(String friendlyName, Path pathModifier) {
		this.friendlyName = friendlyName;
		this.pathModifier = pathModifier;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public Path getPathModifier() {
		return pathModifier;
	}

	@Override
	public String toString() {
		return name();
	}
}

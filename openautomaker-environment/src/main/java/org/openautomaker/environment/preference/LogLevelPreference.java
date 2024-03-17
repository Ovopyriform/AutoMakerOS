package org.openautomaker.environment.preference;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Level;

/**
 * Represents the configured log level for the application.
 * 
 * Simple wrapper tying together the the Preferences API and log4j Level.
 *
 */
public class LogLevelPreference extends AbsPreference<Level> {

	private static final String DEFAULT_VALUE = "INFO";

	/**
	 * Populates the level from the preference. Defaults to INFO
	 */
	public LogLevelPreference() {
		super();
	}

	@Override
	public List<Level> values() {
		Level[] levels = Level.values();
		Arrays.sort(levels);
		return List.of(levels);
	}

	@Override
	public Level get() {
		return Level.getLevel(getUserNode().get(getKey(), DEFAULT_VALUE));
	}

	@Override
	public void set(Level level) {
		getUserNode().put(getKey(), level.name());
	}

}

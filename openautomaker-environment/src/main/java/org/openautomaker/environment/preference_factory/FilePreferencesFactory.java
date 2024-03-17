package org.openautomaker.environment.preference_factory;

import java.io.File;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilePreferencesFactory implements PreferencesFactory {
	private static final String BACK = "..";
	private static final String OPENAUTOMAKER_TEST_ENVIRONMENT = "openautomaker-test-environment";
	private static final String ENV = "env";

	private static final Logger log = LogManager.getLogger();

	Preferences userRootPreferences;

	@Override
	public Preferences systemRoot() {
		return userRoot();
	}

	@Override
	public Preferences userRoot() {
		if (userRootPreferences == null) {
			log.debug("Instantiating user root preferences");

			userRootPreferences = new FilePreferences(null, "");
		}
		return userRootPreferences;
	}

	private static File preferencesFile;

	public static File getPreferencesFile() {
		if (preferencesFile == null) {
			String prefsFile = Paths.get(System.getProperty("user.dir"), BACK, OPENAUTOMAKER_TEST_ENVIRONMENT, ENV, "testenv.prefs").toString();

			preferencesFile = new File(prefsFile).getAbsoluteFile();
			log.info("Preferences file is " + preferencesFile);
		}
		return preferencesFile;
	}
}

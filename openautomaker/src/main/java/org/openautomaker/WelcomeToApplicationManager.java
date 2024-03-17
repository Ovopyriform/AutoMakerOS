package org.openautomaker;

import static org.openautomaker.environment.OpenAutomakerEnv.OPENAUTOMAKER_LAST_VERSION_RUN;

import org.openautomaker.base.BaseLookup;
import org.openautomaker.environment.OpenAutomakerEnv;

import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;

/**
 *
 * @author Ian
 */
class WelcomeToApplicationManager {

	static void displayWelcomeIfRequired() {
		if (!applicationJustInstalled())
			return;

		showWelcomePage();

		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		env.setProperty(OPENAUTOMAKER_LAST_VERSION_RUN, env.getVersion());
	}

	private static boolean applicationJustInstalled() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		return env.getVersion().equals(env.getProperty(OPENAUTOMAKER_LAST_VERSION_RUN));
	}

	private static void showWelcomePage() {
		BaseLookup.getTaskExecutor().runOnGUIThread(() -> {
			ApplicationStatus.getInstance().setMode(ApplicationMode.WELCOME);
		});
	}
}

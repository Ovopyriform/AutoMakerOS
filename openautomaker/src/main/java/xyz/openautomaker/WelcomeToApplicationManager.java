package xyz.openautomaker;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.OPENAUTOMAKER_LAST_VERSION_RUN;

import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import xyz.openautomaker.base.BaseLookup;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author Ian
 */
class WelcomeToApplicationManager {

	static void displayWelcomeIfRequired() {
		if (!applicationJustInstalled())
			return;

		showWelcomePage();

		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		env.setProperty(OPENAUTOMAKER_LAST_VERSION_RUN, env.getVersion());
	}

	private static boolean applicationJustInstalled() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		return env.getVersion().equals(env.getProperty(OPENAUTOMAKER_LAST_VERSION_RUN));
	}

	private static void showWelcomePage() {
		BaseLookup.getTaskExecutor().runOnGUIThread(() -> {
			ApplicationStatus.getInstance().setMode(ApplicationMode.WELCOME);
		});
	}
}

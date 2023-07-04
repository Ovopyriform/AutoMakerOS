
package celtech;

import org.junit.BeforeClass;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author tony
 */
public class JavaFXConfiguredTest extends ConfiguredTest {
	public static class AsNonApp extends Application {

		@Override
		public void start(Stage primaryStage) throws Exception {
			// noop
		}
	}

	public static boolean startedJFX = false;

	@BeforeClass
	public static void initJFX() {
		if (!startedJFX) {
			Thread t = new Thread("JavaFX Init Thread") {
				@Override
				public void run() {
					Application.launch(AsNonApp.class, new String[0]);
				}
			};
			t.setDaemon(true);
			t.start();
			startedJFX = true;
		}
	}

}

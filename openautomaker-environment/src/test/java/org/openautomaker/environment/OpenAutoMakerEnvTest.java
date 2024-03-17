package org.openautomaker.environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.openautomaker.i18n.OpenAutomakerI18N;

class OpenAutomakerEnvTest {

	private static final String TEST_TRANSLATION_KEY = "buttonText.addModel";

	private static final String TEST_PROPERTY_KEY = "test.property.key";
	private static final String TEST_PROPERTY_VALUE = "testPropertyValue";

	private static final String OPENAUTOMAKER = "openautomaker";
	private static final String OPENAUTOMAKER_LOCALE = OPENAUTOMAKER + ".locale";
	private static final String OPENAUTOMAKER_PROPERTIES = OPENAUTOMAKER + ".properties";

	@Test
	void get_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		assertTrue(env != null && env instanceof OpenAutomakerEnv);
	}

	@Test
	void getLocale_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		Locale locale = env.getLocale();

		Properties systemProps = System.getProperties();

		assertEquals(systemProps.getProperty("user.country"), locale.getCountry());
		assertEquals(systemProps.getProperty("user.language"), locale.getLanguage());

		// Set language should alter this

	}

	@Test
	void getRequiredFirmwareVersion_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		assertEquals("781", env.getRequiredFirmwareVersion());
	}


	@Test
	void getVersion_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		assertEquals("4.02.00", env.getVersion());
	}

	@Test
	void getApplicationPath_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		Properties systemProps = System.getProperties();
		assertEquals(Paths.get(systemProps.getProperty("user.dir"), "..", "openautomaker-test-environment", "env", "app", OPENAUTOMAKER).toString(), env.getApplicationPath().toString());

		//TODO: Check named path retrieval
	}

	@Test
	void getUserPath_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		Properties systemProps = System.getProperties();
		assertEquals(Paths.get(systemProps.getProperty("user.dir"), "..", "openautomaker-test-environment", "env", "usr", OPENAUTOMAKER).toString(), env.getUserPath().toString());

		//TODO: Check named path retrieval
	}

	@Test
	void getName_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		assertEquals("OpenAutomaker.org: Test Environment", env.getName());
	}

	@Test
	void getShortName_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();
		assertEquals("OpenAutomaker: Test Environment", env.getShortName());
	}

	@Test
	void getI18N_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();

		// Get the expected transialtion instance directly
		OpenAutomakerI18N expectedI18N = new OpenAutomakerI18N(env.getLocale());

		// Get the translation instance from environment
		OpenAutomakerI18N actualI18N = OpenAutomakerEnv.getI18N();

		//Compare a created i18n object against the i18n object from AutoMakerEnvironment
		assertEquals(expectedI18N.t(TEST_TRANSLATION_KEY), actualI18N.t(TEST_TRANSLATION_KEY));

	}

	@Test
	void propertyAccessors_test() {
		OpenAutomakerEnv env = OpenAutomakerEnv.get();

		env.setProperty(TEST_PROPERTY_KEY, TEST_PROPERTY_VALUE);
		assertEquals(TEST_PROPERTY_VALUE, env.getProperty(TEST_PROPERTY_KEY));

		assertEquals(TEST_PROPERTY_VALUE, env.removeProperty(TEST_PROPERTY_KEY));
		assertNull(env.getProperty(TEST_PROPERTY_KEY));

	}
}

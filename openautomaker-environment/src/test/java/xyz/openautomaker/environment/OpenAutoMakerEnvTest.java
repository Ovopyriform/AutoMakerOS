package xyz.openautomaker.environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import xyz.openautomaker.i18n.OpenAutoMakerI18N;

class OpenAutoMakerEnvTest {

	private static final String TEST_TRANSLATION_KEY = "buttonText.addModel";

	private static final String TEST_PROPERTY_KEY = "test.property.key";
	private static final String TEST_PROPERTY_VALUE = "testPropertyValue";

	private static final String OPENAUTOMAKER = "openautomaker";
	private static final String OPENAUTOMAKER_LOCALE = OPENAUTOMAKER + ".locale";
	private static final String OPENAUTOMAKER_PROPERTIES = OPENAUTOMAKER + ".properties";

	@Test
	void get_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		assertTrue(env != null && env instanceof OpenAutoMakerEnv);
	}

	@Test
	void getLocale_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		Locale locale = env.getLocale();

		Properties systemProps = System.getProperties();

		assertEquals(systemProps.getProperty("user.country"), locale.getCountry());
		assertEquals(systemProps.getProperty("user.language"), locale.getLanguage());

		// Set language should alter this

	}

	@Test
	void getRequiredFirmwareVersion_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		assertEquals("781", env.getRequiredFirmwareVersion());
	}

	@Test
	void isDebugEnabled_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		assertEquals(true, env.isDebugEnabled());
	}

	@Test
	void getVersion_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		assertEquals("4.02.00", env.getVersion());
	}

	@Test
	void setLocale_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		Locale currentLocal = env.getLocale();

		env.setLocale(Locale.FRENCH);

		Properties userProps = new Properties();
		try (InputStream is = Files.newInputStream(env.getUserPath(OPENAUTOMAKER_PROPERTIES))) {
			userProps.load(is);
		} catch (IOException e) {
			fail("Could not load user properties");
		}

		String actualLanguageTag = userProps.getProperty(OPENAUTOMAKER_LOCALE);
		env.setLocale(currentLocal); // Reset locale

		assertEquals(Locale.FRENCH.toLanguageTag(), actualLanguageTag);
	}

	@Test
	void getApplicationPath_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		Properties systemProps = System.getProperties();
		assertEquals(Paths.get(systemProps.getProperty("user.dir"), "..", "openautomaker-test-environment", "env", "app", OPENAUTOMAKER).toString(), env.getApplicationPath().toString());

		//TODO: Check named path retrieval
	}

	@Test
	void getUserPath_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		Properties systemProps = System.getProperties();
		assertEquals(Paths.get(systemProps.getProperty("user.dir"), "..", "openautomaker-test-environment", "env", "usr", OPENAUTOMAKER).toString(), env.getUserPath().toString());

		//TODO: Check named path retrieval
	}

	@Test
	void getName_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		assertEquals("OpenAutoMaker.XYZ: Test Environment", env.getName());
	}

	@Test
	void getShortName_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		assertEquals("OpenAutoMaker: Test Environment", env.getShortName());
	}

	@Test
	void getI18N_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();

		// Get the expected transialtion instance directly
		OpenAutoMakerI18N expectedI18N = new OpenAutoMakerI18N(env.getLocale());

		// Get the translation instance from environment
		OpenAutoMakerI18N actualI18N = OpenAutoMakerEnv.getI18N();

		//Compare a created i18n object against the i18n object from AutoMakerEnvironment
		assertEquals(expectedI18N.t(TEST_TRANSLATION_KEY), actualI18N.t(TEST_TRANSLATION_KEY));

	}

	@Test
	void propertyAccessors_test() {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();

		env.setProperty(TEST_PROPERTY_KEY, TEST_PROPERTY_VALUE);
		assertEquals(TEST_PROPERTY_VALUE, env.getProperty(TEST_PROPERTY_KEY));

		assertEquals(TEST_PROPERTY_VALUE, env.removeProperty(TEST_PROPERTY_KEY));
		assertNull(env.getProperty(TEST_PROPERTY_KEY));

	}
}

package xyz.openautomaker.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

class OpenAutoMakerI18NTest {

	//@formatter:off
	
	// List of keys to test
	private static final List<String> TRANSLATION_KEYS = List.of(
			"buttonText.addModel",
			"buttonText.addModelFromCloud",
			"buttonText.addToProject",
			"buttonText.ambientLights",
			"buttonText.autoLayout"
			);
	
	
	// English Translations
	private static final Map<String, String> ENGLISH = Map.of(
			"buttonText.addModel", "Add Model",
			"buttonText.addModelFromCloud", "From Cloud",
			"buttonText.addToProject", "Add To Project",
			"buttonText.ambientLights", "Ambient",
			"buttonText.autoLayout", "Auto Layout"
		);
	
	//French Translations
	private static final Map<String, String> FRENCH = Map.of(
			"buttonText.addModel","Ajouter un modèle",
			"buttonText.addModelFromCloud","Depuis le cloud",
			"buttonText.addToProject","Ajouter au projet",
			"buttonText.ambientLights","Ambiante",
			"buttonText.autoLayout","Mise en page automatique"
		);
	
	// German Translations
	private static final Map<String, String> GERMAN = Map.of(
			"buttonText.addModel","Modell hinzufügen",
			"buttonText.addModelFromCloud","Vom Cloud-Speicher",
			"buttonText.addToProject","Zum Projekt hinzufügen",
			"buttonText.ambientLights","Umgebung",
			"buttonText.autoLayout","Auto Layout"
		);
	
	// Spanish Translations
	public static final Map<String, String> SPANISH = Map.of(
			"buttonText.addModel","Añadir Modelo",
			"buttonText.addModelFromCloud","De La Nube",
			"buttonText.addToProject","Añadir Al Proyecto",
			"buttonText.ambientLights","Ambiente",
			"buttonText.autoLayout","Acomodo Automático"
		);

	private static final Map<Locale, Map<String, String>> EXPECTED = Map.of(
			Locale.ENGLISH, ENGLISH,
			Locale.FRENCH, FRENCH,
			Locale.GERMAN, GERMAN,
			Locale.forLanguageTag("es"), SPANISH
		);

	
	// Expected template replacements (english only)
	private static final Map<String, String> EXPECTED_TEMPLATES = Map.of(
			"*T01","Disconnect your machine from USB and AC power. Check your USB cable is connected correctly.",
			"*T02","If this error persists then revert to older version of firmware / AutoMaker.",
			"*T03","Disconnect your machine from USB and AC power. Check that your SD card is present and seated correctly."
		);
	//@formatter:on

	@Test
	void t_defaultLocale_test() {

		// Check that 
		OpenAutoMakerI18N i18n = new OpenAutoMakerI18N();
		OpenAutoMakerI18N defaultLocaleI18N = new OpenAutoMakerI18N(Locale.getDefault());

		for (String translationKey : TRANSLATION_KEYS) {
			assertEquals(defaultLocaleI18N.t(translationKey), i18n.t(translationKey));
		}

	}

	@Test
	void t_definedLocale_test() {
		for (Locale locale : EXPECTED.keySet()) {
			Map<String, String> expectedValues = EXPECTED.get(locale);

			OpenAutoMakerI18N i18n = new OpenAutoMakerI18N(locale);

			for (String translationKey : expectedValues.keySet()) {
				assertEquals(expectedValues.get(translationKey), i18n.t(translationKey));
			}
		}
	}

	@Test
	void substituteTemplates_test() {
		OpenAutoMakerI18N i18n = new OpenAutoMakerI18N(Locale.ENGLISH);

		for (String templateKey : EXPECTED_TEMPLATES.keySet()) {
			assertEquals(EXPECTED_TEMPLATES.get(templateKey), i18n.substituteTemplates(templateKey));
		}
	}

	// Add test for currency when it's available

}

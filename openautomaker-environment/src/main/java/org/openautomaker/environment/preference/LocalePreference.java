package org.openautomaker.environment.preference;

import java.util.List;
import java.util.Locale;

/**
 * Preference representing the users choice of locale
 */
public class LocalePreference extends AbsPreference<Locale> {

	private Locale fDefaultLocale;

	// Supported languages
	private static final List<Locale> VALUES = List.of(
			Locale.UK,
			Locale.US,
			Locale.GERMANY,
			Locale.FRANCE,
			Locale.JAPAN,
			Locale.KOREA,
			Locale.CHINA,
			Locale.TAIWAN,
			Locale.forLanguageTag("tr-TR"), //Turkey
			Locale.forLanguageTag("es-ES"), //Spain
			Locale.forLanguageTag("fi-FI"), //Finland
			Locale.forLanguageTag("ru-RU"), //Russia
			Locale.forLanguageTag("sv-SE"), //Sweden
			Locale.forLanguageTag("zh-HK"), //Hong Kong
			Locale.forLanguageTag("zh-SG") // Singapore
	);

	public LocalePreference() {
		super();
		// Check system Locale.  If it's not in the list default to en-gb
		fDefaultLocale = Locale.getDefault();
		if (!VALUES.contains(fDefaultLocale))
			fDefaultLocale = Locale.UK;

	}

	@Override
	public Locale get() {
		return Locale.forLanguageTag(getUserNode().get(getKey(), fDefaultLocale.toLanguageTag()));
	}

	@Override
	public void set(Locale locale) {
		getUserNode().put(getKey(), locale.toLanguageTag());
	}

	@Override
	public List<Locale> values() {
		return VALUES;
	}
}
